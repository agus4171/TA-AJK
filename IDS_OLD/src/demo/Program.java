/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package demo;

import java.util.ArrayList;

/**
 *
 * @author Agus
 */
public class Program {
    public static void main(String[] args) {

	ArrayList<Integer> list = new ArrayList<>();
	list.add(7);
	list.add(8);
	list.add(9);

	// Create an empty array and pass to toArray.
	Integer[] array = {};
	array = list.toArray(array);

	// Our array now has the ArrayList's elements.
	for (int elem : array) {
	    System.out.println(elem);
	}
    }
}
