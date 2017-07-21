package com.ji.spark;

import java.util.Arrays;
import java.util.List;

/**
 * Unit test for simple App.
 */
public class AppTest {
	public static void main(String[] args) {
		List<Integer> costBeforeTax = Arrays.asList(100, 200, 300, 400, 500);
		costBeforeTax.stream().map((cost) -> cost + 12 * cost).forEach((result) -> {
			System.out.println(result);
		});
		// costBeforeTax.stream().map((cost) -> cost + .12*cost).forEach(System.out::println);
	}

}
