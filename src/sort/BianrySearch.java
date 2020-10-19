package Sort;

public class BianrySearch {
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
        ob.binarySearch(arr);
        ob.printArray(arr);
    }
}
