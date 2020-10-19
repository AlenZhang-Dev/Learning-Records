package Sort;

public class QuickSort {
    int partition(int arr[], int low, int high){
        int pivot = arr[low];
        while(low < high){
            //find the element smaller than pivot
            while(low < high && arr[high] >= pivot)
                high--;
            arr[low] = arr[high];
            while(low < high && arr[low] <= pivot)
                low++;
            arr[high] = arr[low];
        }
        arr[low] = pivot;
        return low;
    }

    void quickSort(int arr[], int low, int high){
        if(low < high){
            int pivotLoc = partition(arr, low, high);
            quickSort(arr, low, pivotLoc - 1);
            quickSort(arr, pivotLoc + 1, high);
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
        int arr[] = {12, 11, 13, 5, 6, 22, 20, 22};
        QuickSort ob = new QuickSort();
        //ob.sort(arr);
        ob.quickSort(arr, 0, arr.length - 1);
        ob.printArray(arr);
    }

}
