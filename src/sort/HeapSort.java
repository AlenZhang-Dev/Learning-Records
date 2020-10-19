package Sort;

import java.util.Arrays;

public class HeapSort {
    public static void main(String[] args) {
        int[] arr = {11, 9, 8, 7, 6, 5, 4, 3, 2, 1};
        heap_sort(arr);
        System.out.println(Arrays.toString(arr));
    }

    public static void heap_sort(int[] arr) {
        //Build heap
        for (int i = arr.length / 2 - 1; i >= 0; i--) {
            max_heapify(arr, i, arr.length);
        }
        //Sort Process, extract an element one by one.
        for (int j = arr.length - 1; j > 0; j--) {
            //Move current root to end.
            swap(arr, 0, j);
            //Rebuild the heap.
            max_heapify(arr, 0, j);
        }
    }

    //adjust the heap, the core function of HeapSort;
    //To heapify a subtree from start to end.
    public static void max_heapify(int[] arr, int start, int end) {
        //keep the current swap element.
        int temp = arr[start];
        //compare the child, from left to right
        for (int i = start * 2 + 1; i < end; i = i * 2 + 1) {
            //select the larger one between right child and left child
            if (i + 1 < end && arr[i] < arr[i + 1]) {
                i++;
            }
            //swap the root node with larger one.
            if (arr[i] > temp) {
                arr[start] = arr[i];
                start = i;
            } else {
                break;
            }
        }
        arr[start] = temp;
    }

    //swap the element
    public static void swap(int[] arr, int a, int b) {
        int temp = arr[a];
        arr[a] = arr[b];
        arr[b] = temp;
    }
}
