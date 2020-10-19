package Sort;

public class BubbleSort {
    public void bubbleSort(int arr[]) {
        int length = arr.length;
        for(int i = 0; i < length - 1; ++i){
            for(int j = 0; j < length - i - 1; ++j){
                //Move the bigger one to the end.
                if(arr[j] > arr[j + 1])
                    swap(arr, j, j + 1);
            }
        }
    }

    public void printArray(int arr[]) {
        int n = arr.length;
        for (int i = 0; i < n; ++i) {
            System.out.print(arr[i] + " ");
        }

        System.out.println();
    }

    public void swap(int arr[], int a, int b) {
        arr[a] = arr[a] + arr[b];
        arr[b] = arr[a] - arr[b];
        arr[a] = arr[a] - arr[b];
    }

    public static void main(String arg[]) {
        int brr[] = {12, 11};
        int arr[] = {12, 11, 13, 5, 6};
        BubbleSort ob = new BubbleSort();
        //ob.sort(arr);
        ob.bubbleSort(arr);
        ob.printArray(arr);
    }
}
