package com.example.asteroids;

import java.util.LinkedList;

public class Queue<T> {
	private LinkedList<T> list;

	public Queue(){
		list = new LinkedList<T>();
	}

	public void push(T n, boolean priority) {
		if(priority) {
			list.addFirst(n);
		}
		else {
			list.add(n);
		}
	}

	public T peek() {
		return list.getFirst();
	}

	public T pop() {
		T pop = list.getFirst();
		list.removeFirst();
		return pop;
	}
}
