package Sort;

public class SelectionSort {
    void selectionSort(int arr[]){
        int length = arr.length;
        for(int i = 0; i < length - 1; ++i){
            int min = i;
            for(int j = i + 1; j < length; ++j){
                if(arr[j] < arr[min])
                    min = j;
            }
            if(min != i)
                swap(arr, min, i);
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
        SelectionSort ob = new SelectionSort();
        //ob.sort(arr);
        ob.selectionSort(arr);
        ob.printArray(arr);
    }
}
