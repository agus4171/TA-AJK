using System;
using System.Collections.Generic;
using System.Text;
using System.IO;
using Tamir.IPLib.Packets;
using Tamir.IPLib;
using System.Reflection;

namespace TcpRecon
{
    /// <summary>
    /// Holds the connection information of the Tcp session
    /// </summary>
    class Connection
    {
        private string m_srcIp;
        public string SourceIp
        {
            get { return m_srcIp; }
        }

        private ushort m_srcPort;
        public ushort SourcePort
        {
            get { return m_srcPort; }
        }

        private string m_dstIp;
        public string DestinationIp
        {
            get { return m_dstIp; }
        }

        private ushort m_dstPort;
        public ushort DestinationPort
        {
            get { return m_dstPort; }
        }

        public Connection(string sourceIP, UInt16 sourcePort, string destinationIP, UInt16 destinationPort)
        {
            m_srcIp = sourceIP;
            m_dstIp = destinationIP;
            m_srcPort = sourcePort;
            m_dstPort = destinationPort;
        }

        public Connection(Tamir.IPLib.Packets.TCPPacket packet)
        {
            m_srcIp = packet.SourceAddress; 
            m_dstIp = packet.DestinationAddress;
            m_srcPort = (ushort)packet.SourcePort;
            m_dstPort = (ushort)packet.DestinationPort;
        }

        /// <summary>
        /// Overrided in order to catch both sides of the connection 
        /// with the same connection object
        /// </summary>
        /// <param name="obj"></param>
        /// <returns></returns>
        public override bool Equals(object obj)
        {
            if (!(obj is Connection))
                return false;
            Connection con = (Connection)obj;

            bool result = ((con.SourceIp.Equals(m_srcIp)) && (con.SourcePort == m_srcPort) && (con.DestinationIp.Equals(m_dstIp)) && (con.DestinationPort == m_dstPort)) ||
                ((con.SourceIp.Equals(m_dstIp)) && (con.SourcePort == m_dstPort) && (con.DestinationIp.Equals(m_srcIp)) && (con.DestinationPort == m_srcPort));

            return result;
        }

        public override int GetHashCode()
        {
            return ((m_srcIp.GetHashCode() ^ m_srcPort.GetHashCode()) as object).GetHashCode() ^
                ((m_dstIp.GetHashCode() ^ m_dstPort.GetHashCode()) as object).GetHashCode();
        }

        public string getFileName(string path)
        {
            return string.Format("{0}{1}.{2}-{3}.{4}.data", path,  m_srcIp , m_srcPort, m_dstIp, m_dstPort);
        }
    }

    class Program
    {
        // Holds the file streams for each tcp session in case we use libnids
        static Dictionary<Connection, FileStream> nidsDict;
        // Holds the file streams for each tcp session in case we use SharpPcap
        static Dictionary<Connection, TcpRecon> sharpPcapDict = new Dictionary<Connection, TcpRecon>();
        // The directory path of the Pcap file
        static string path = "";

        #region "Callback Functions"
        // The callback function for the managedLibnids library
        static void handleData(byte[] arr, UInt32 sourceIP, UInt16 sourcePort, UInt32 destinationIP, UInt16 destinationPort, bool urgent)
        {
            System.Net.IPAddress srcIp = new System.Net.IPAddress(sourceIP);
            System.Net.IPAddress dstIp = new System.Net.IPAddress(destinationIP);
            // Creates a key for the dictionary
            Connection c = new Connection(srcIp.ToString(), sourcePort, dstIp.ToString(), destinationPort);

            // create a new entry if the key does not exists
            if (!nidsDict.ContainsKey(c))
            {
                string fileName = c.getFileName(path);
                FileStream fStream = new FileStream(fileName, FileMode.Create);
                nidsDict.Add(c, fStream);
            }

            // write the new data to file
            nidsDict[c].Write(arr, 0, arr.Length); 
        }

        // The callback function for the SharpPcap library
        private static void device_PcapOnPacketArrival(object sender, Packet packet)
        {
            if (!(packet is TCPPacket)) return;

            TCPPacket tcpPacket = (TCPPacket)packet;
            // Creates a key for the dictionary
            Connection c = new Connection(tcpPacket);

            // create a new entry if the key does not exists
            if (!sharpPcapDict.ContainsKey(c))
            {
                string fileName = c.getFileName(path);
                TcpRecon tcpRecon = new TcpRecon(fileName);
                sharpPcapDict.Add(c, tcpRecon);
            }

            // Use the TcpRecon class to reconstruct the session
            sharpPcapDict[c].ReassemblePacket(tcpPacket);
        }
        #endregion

        // A delegate to the reconstruction function the user has chosen
        delegate void ReconFunc(string capFile);

        /// <summary>
        /// Reconstruct a Pcap file using libnids
        /// </summary>
        /// <param name="capFile"></param>
        static void ReconSingleFileLibNids(string capFile)
        {
            FileInfo fi = new FileInfo(capFile);
            path = fi.DirectoryName + "\\";
            // register the local callback and start libnids
            managedLibnids.LibnidsWrapper.Run(capFile, new DataCallbackDelagate(handleData), new DataCallbackDelagate(handleData));
            // Clean up
            foreach (FileStream fs in nidsDict.Values)
            {
                fs.Close();
            }
            nidsDict.Clear();
        }
        
        /// <summary>
        /// Reconstruct a Pcap file using TcpRecon class
        /// </summary>
        /// <param name="capFile"></param>
        static void ReconSingleFileSharpPcap(string capFile)
        {
            PcapDevice device;

            FileInfo fi = new FileInfo(capFile);
            path = fi.DirectoryName + "\\";
            try
            {
                //Get an offline file pcap device
                device = SharpPcap.GetPcapOfflineDevice(capFile);
                //Open the device for capturing
                device.PcapOpen();
            }
            catch (Exception e)
            {
                Console.WriteLine(e.Message);
                return;
            }

            //Register our handler function to the 'packet arrival' event
            device.PcapOnPacketArrival +=
                new SharpPcap.PacketArrivalEvent(device_PcapOnPacketArrival);

            // FOR THIS LINE TO WORK YOU NEED TO CHANGE THE
            // SHARPPCAP LIBRARY MANUALLY AND REMOVE PcapSetFilter method
            // FROM PcapOfflineDevice
            //
            // add a filter so we get only tcp packets
            // device.PcapSetFilter("tcp");

            //Start capture 'INFINTE' number of packets
            //This method will return when EOF reached.
            device.PcapCapture(SharpPcap.INFINITE);

            //Close the pcap device
            device.PcapClose();

            // Clean up
            foreach (TcpRecon tr in sharpPcapDict.Values)
            {
                tr.Close();
            }
            sharpPcapDict.Clear();
        }

        static void Main(string[] args)
        {
            if (args.Length < 1) 
            {
                Console.WriteLine(@"Usage: TcpRecon <pcap file> [-nids]
    -nids               -- Uses libnids for the tcp reconstruction
e.g:    TcpRecon C:\PcapFileDir\SomePcapFile.pcap
");
                return;
            }

            DateTime startTime = DateTime.Now;
            ReconFunc reconFunc = null;

            // decide which library to use
            if (args.Length > 1)
            {
                if (args[1].Equals("-nids"))
                {
                    reconFunc = new ReconFunc(ReconSingleFileLibNids);
                    nidsDict = new Dictionary<Connection, FileStream>();
                }
            }
            
            // we are using the built in functionality
            if (reconFunc == null)
            {
                reconFunc = new ReconFunc(ReconSingleFileSharpPcap);
                sharpPcapDict = new Dictionary<Connection, TcpRecon>();
            }

            string capFile = args[0];
            if (!System.IO.File.Exists(capFile))
            {
                Console.WriteLine("Pcap file not found!");
                return;
            }

            // start the chosen capturing library
            reconFunc(capFile);
                        
            DateTime finishTime = DateTime.Now;
            TimeSpan totalTime = (finishTime - startTime);

            Console.WriteLine(string.Format("\nTotal reconstruct time: {0} seconds", totalTime.TotalSeconds));

        }

    }
}
