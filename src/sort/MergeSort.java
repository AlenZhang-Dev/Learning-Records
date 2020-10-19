package Sort;

public class MergeSort {
    void merge(int arr[], int l, int m, int r){
        //count the length of two arrays
        int n1 = m - l + 1;
        int n2 = r - m;
        int arr1[] = new int[n1];
        int arr2[] = new int[n2];

        //copy the data to the two array
        for(int i = 0; i < n1; i++)
            arr1[i] = arr[l + i];
        for(int i = 0; i < n2; i++)
            arr2[i] = arr[m + 1 + i];

        //merge begin
        int i = 0, j = 0;
        int k = l;
        while(i < n1 && j < n2){
            if(arr1[i] < arr2[j])
                arr[k++] = arr1[i++];
            else
                arr[k++] = arr2[j++];
        }
        while(i < n1)
            arr[k++] = arr1[i++];

        while(j < n2)
            arr[k++] = arr2[j++];

    }
    //divide the array
    void sort (int arr[], int l, int r){
        if(l < r){
            int m = (l + r) / 2;
            sort(arr, l, m);
            sort(arr,m + 1, r);

            merge(arr, l, m, r);
        }
    }

    static void printArray(int arr[])
    {
        int n = arr.length;
        for (int i = 0; i < n; ++i)
            System.out.print(arr[i] + " ");
        System.out.println();
    }

    // Driver method
    public static void main(String args[])
    {
        int arr[] = { 12, 11, 13, 5, 6, 7 };

        System.out.println("Given Array");
        printArray(arr);

        MergeSort ob = new MergeSort();
        ob.sort(arr, 0, arr.length - 1);

        System.out.println("\nSorted array");
        printArray(arr);
    }
}
