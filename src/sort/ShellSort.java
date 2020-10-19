package Sort;

public class ShellSort {
    //交换
    public void sort(int arr[]) {
        for (int gap = arr.length / 2; gap > 0; gap /= 2) {
            for (int i = gap; i < arr.length; i++) {
                //每次都与相隔gap位的值进行比较
                int j = i;
                while (j - gap >= 0 && arr[j] < arr[j - gap]) {
                    //swap the element
                    swap(arr, j, j - gap);
                    j -= gap;
                }
            }
        }
    }

    //移动
    public void sortMove(int arr[]) {
        for (int gap = arr.length / 2; gap > 0; gap /= 2) {
            for (int i = gap; i < arr.length; i++) {
                //每次都与相隔gap位的值进行比较
                int j = i;
                int temp = arr[i];
                while (j - gap >= 0 && arr[j] < arr[j - gap]) {
                    arr[j] = arr[j - gap];
                    j -= gap;
                }
                arr[j] =temp;
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
        ShellSort ob = new ShellSort();
        //ob.sort(arr);
        ob.sortMove(arr);
        ob.printArray(arr);
    }
}
