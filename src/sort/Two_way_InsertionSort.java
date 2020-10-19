package Sort;

public class Two_way_InsertionSort {
    public void twoWayInsertionSort(int arr[], int brr[]){
        int length = arr.length;
        int first =0, last = 0;//point to the greater one and tiny one.
        brr[0] = arr[0];
        for(int i = 1; i < length; ++i){
            //greater than the biggest one
            if(arr[i] >= brr[last]){
                brr[++last] = arr[i];
            }
            else if(arr[i] <= brr[first]){
                first = (first - 1 + length) % length;
                brr[first] = arr[i];
            }
            else{
//                int j = (last - 1 + length) % length;
                int j = last;
                while(brr[j] > arr[i]){
                    brr[(j + 1) % length] = brr[j];//move back element
                    j = (j - 1 + length) % length;//update point
                }
                brr[(j + 1) % length] = arr[i];
                last++;
            }
            System.out.println("First is " + first + "Last is " + last);
        }
        //copy back to the array
        for(int i = 0; i < length; ++i){
            arr[i] = brr[(first + i) % length];
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
        int arr[] = {12, 11, 13, 5, 6, 7, 18};
        int brr[] = new int[arr.length];
        Two_way_InsertionSort ob = new Two_way_InsertionSort();
        //ob.sort(arr);
        ob.twoWayInsertionSort(arr, brr);
        ob.printArray(arr);
    }
}
