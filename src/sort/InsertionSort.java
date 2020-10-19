package Sort;

public class InsertionSort {
    //折半插入
    void binarySearch(int arr[]) {
        for (int i = 1; i < arr.length; ++i) {
            int temp = arr[i];
            int low = 0;
            int high = i - 1;
            while (low <= high) {
                int m = low + high / 2;
                if (arr[m] > temp)
                    high = m - 1;
                else
                    low = m + 1;
            }
            for (int j = i - 1; j >= low; --j) {
                arr[j + 1] = arr[j];
            }
            arr[low] = temp;
        }
    }

    void sort(int arr[]) {
//        for(int i = 1; i < arr.length; ++i){
//            int temp = arr[i];
//            int j = i - 1;
//
//            while( j >= 0 && arr[j] > temp) {
//                arr[j + 1] = arr[j];
//                j--;
//            }
//            arr[j + 1] = temp;
//        }

        for (int i = 1; i < arr.length; ++i) {
            int temp = arr[i];
            int j;
            for (j = i - 1; j >= 0 && arr[j] > temp; --j)
                arr[j + 1] = arr[j];
            arr[++j] = temp;
        }
    }

    public void printArray(int arr[]) {
        int n = arr.length;
        for (int i = 0; i < n; ++i) {
            System.out.print(arr[i] + " ");
        }

        System.out.println();
    }

    public static void main(String arg[]) {
        int brr[] = {12, 11};
        int arr[] = {12, 11, 13, 5, 6};
        InsertionSort ob = new InsertionSort();
        //ob.sort(arr);
        ob.binarySearch(brr);
        ob.printArray(brr);
    }

}
