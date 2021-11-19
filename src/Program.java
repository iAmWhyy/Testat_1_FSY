import java.util.*;

class Program {
    public static void main(String[] args) {
        var temp = new SortAlgorithmsComparator();
        temp.compareAllAvailableSortAlgorithms(
                new int[]{10, 1000, 100000},
                new int[]{10, 100000},
                OrderAndUniqueness.values(),
                0.1
        );
    }
}

interface SortAlgorithm {
    void sort(int[] arr, int maxNumberInArr);
}

class ArrayInputCreator {

    private ArrayInputCreator() {}

    private static final Random rnd = new Random();

    public static int[] createArrayWithRndValues(int arrLength, int maxNumberInArr) //absolut randomized, duplicate values possible
    {
        int[] array = new int[arrLength];
        for (int i = 0; i < array.length; i++) {
            array[i] = rnd.nextInt(maxNumberInArr);
        }
        return array;
    }

    public static int[] createArrayWithRndValues(int arrLength, int maxNumberInArr, double relativeProportionOfIdenticalElements)
    {
        if (relativeProportionOfIdenticalElements > 1 ||
                relativeProportionOfIdenticalElements < 0){
            throw new IllegalArgumentException("value of relativeProportionOfIdenticalElements has to be between 0 and 1");
        }
        if (maxNumberInArr < 1){
            throw new IllegalArgumentException("value of maxNumberInArr has to be greater than 0");
        }
        int[] array = new int[arrLength];
        long numberOfIdenticalElementsInArr = Math.round(arrLength*relativeProportionOfIdenticalElements);
        var identicalElement = rnd.nextInt(maxNumberInArr);
        for (int i = 0; i < array.length; i++) //fill array with suitable values
        {
            if (numberOfIdenticalElementsInArr > 0){
                array[i] = identicalElement;
                numberOfIdenticalElementsInArr--;
            }
            else{
                do {
                    array[i] = rnd.nextInt(maxNumberInArr);
                }while (array[i] == identicalElement);
            }
        }
        shuffleArray(array);
        return array;
    }

    private static void shuffleArray(int[] ar)
    {
        for (int i = ar.length - 1; i > 0; i--)
        {
            int index = rnd.nextInt(i + 1);
            Swapper.swap(ar, index, i);
        }
    }

    public static int[] createArraySortedAscending(int arrLength, int maxIntInArr)
    {
        int[] array = new int[arrLength];
        for (int i = 0; i < array.length; i++) {
            array[i] = (int) (i * (long) maxIntInArr / array.length);
        }
        return array;
    }

    public static int[] createArraySortedDescending(int arrLength, int maxIntInArr)
    {
        int[] array = new int[arrLength];
        for (int i = 0; i < array.length; i++) {
            array[i] = (int)((array.length - 1 - i) * (long) maxIntInArr / array.length);
        }
        return array;
    }

    public static int[] createArraySortedExceptOneSwap(int arrLength, int maxIntInArr)
    {
        int[] sortedArray = createArraySortedAscending(arrLength, maxIntInArr);
        int pos1 = rnd.nextInt(arrLength);
        int pos2;
        do {
            pos2 = rnd.nextInt(arrLength);
        }while (pos1 == pos2);
        Swapper.swap(sortedArray, pos1, pos2);
        return sortedArray;
    }
}

class SortAlgorithmFactory {
    public SortAlgorithm[] getAllSortAlgorithms(){
        return new SortAlgorithm[]{
                new MergeSort(),
                new RandomizedQuickSort(),
                new CountingSort(),
                new HeapSort()
        };
    }
}

class SortAlgorithmsComparator {
    public void compareAllAvailableSortAlgorithms(
            int[] arrayLengths,
            int[] maxIntsInArr,
            OrderAndUniqueness[] orderAndUniquenesses,
            double shareOfIdenticalElements){
        SortAlgorithmFactory sortAlgorithmFactory = new SortAlgorithmFactory();
        SortAlgorithm[] listOfAvailableSortAlgorithms = sortAlgorithmFactory.getAllSortAlgorithms();

        for (int arrayLength : arrayLengths) {
            for (int maxIntInArr : maxIntsInArr) {
                for (var orderAndUniqueness : orderAndUniquenesses) {
                    ArraySpecification arraySpecification = new ArraySpecification(arrayLength, maxIntInArr, orderAndUniqueness, shareOfIdenticalElements);
                    int[] arrayToSort = null;
                    switch (orderAndUniqueness){
                        case SortedAscending:
                            if (maxIntInArr <= arrayLength)
                                arrayToSort = ArrayInputCreator.createArraySortedAscending(arrayLength, maxIntInArr);
                            break;
                        case SortedDescending:
                            if (maxIntInArr <= arrayLength)
                                arrayToSort = ArrayInputCreator.createArraySortedDescending(arrayLength, maxIntInArr);
                            break;
                        case AscendingOneRandomSwap:
                            if (maxIntInArr <= arrayLength)
                                arrayToSort = ArrayInputCreator.createArraySortedExceptOneSwap(arrayLength, maxIntInArr);
                            break;
                        case Random:
                            arrayToSort = ArrayInputCreator.createArrayWithRndValues(arrayLength, maxIntInArr);
                            break;
                        case Random_SomeElementsIdentical:
                            arrayToSort = ArrayInputCreator.createArrayWithRndValues(arrayLength, maxIntInArr, shareOfIdenticalElements);
                            break;
                    }

                    if (arrayToSort != null){
                        SortAlgorithmAndRuntime[] sortAlgorithmRuntimes =
                                getRuntimesOfAlgorithm(arrayToSort, maxIntInArr, listOfAvailableSortAlgorithms);
                        ArrayAndSortAlgorithmsWithRuntime arrayAndSortAlgorithmsWithRuntime =
                                new ArrayAndSortAlgorithmsWithRuntime(arraySpecification, arrayToSort, sortAlgorithmRuntimes);
                        System.out.print(arrayAndSortAlgorithmsWithRuntime);
                    }
                }
            }
        }
    }

    private SortAlgorithmAndRuntime[] getRuntimesOfAlgorithm(int[] arrayToSort, int maxIntInArr, SortAlgorithm[] sortAlgorithms) {
        var sortAlgorithmAndRuntimes = new SortAlgorithmAndRuntime[sortAlgorithms.length];

        for (int i = 0; i < sortAlgorithms.length; i++) {
            String algoName = sortAlgorithms[i].getClass().getSimpleName();
            var array = arrayToSort.clone();
            var startTime = System.nanoTime();

            sortAlgorithms[i].sort(array, maxIntInArr);
            double time = (System.nanoTime() - startTime)/1000.0;

            sortAlgorithmAndRuntimes[i] = new SortAlgorithmAndRuntime(algoName, time);
        }

        return sortAlgorithmAndRuntimes;
    }
}

class SortAlgorithmAndRuntime {
    public String algorithmName;
    public double time;

    public SortAlgorithmAndRuntime(String algorithmName, double time){
        this.algorithmName = algorithmName;
        this.time = time;
    }

    @Override
    public String toString(){
        return algorithmName + ";" + time;
    }
}

class ArrayAndSortAlgorithmsWithRuntime{
    public ArraySpecification arraySpecs;
    public int[] array;
    public SortAlgorithmAndRuntime[] sortAlgorithmAndRuntimes;

    public ArrayAndSortAlgorithmsWithRuntime(ArraySpecification arraySpecs, int[] array, SortAlgorithmAndRuntime[] sortAlgorithmAndRuntimes) {
        this.arraySpecs = arraySpecs;
        this.array = array;
        this.sortAlgorithmAndRuntimes = sortAlgorithmAndRuntimes;
    }

    @Override
    public String toString(){
        StringBuilder stringBuilder = new StringBuilder();
        for (var data : sortAlgorithmAndRuntimes){
            stringBuilder.append(String.format("%s;%s\n", arraySpecs, data));
        }
        return stringBuilder.toString();
    }
}

class ArraySpecification {
    int length, maxInt;
    OrderAndUniqueness orderAndUniqueness;
    double relativeProportionOfIdenticalElements;

    public ArraySpecification(int length, int maxInt, OrderAndUniqueness orderAndUniqueness, double relativeProportionOfIdenticalElements) {
        this.length = length;
        this.maxInt = maxInt;
        this.orderAndUniqueness = orderAndUniqueness;
        this.relativeProportionOfIdenticalElements = relativeProportionOfIdenticalElements;
    }

    @Override
    public String toString(){
        String proportion = orderAndUniqueness == OrderAndUniqueness.Random_SomeElementsIdentical
                ? ": " + relativeProportionOfIdenticalElements
                : "";
        return String.format("%d;%d;%s", length, maxInt, orderAndUniqueness + proportion);
    }
}

enum OrderAndUniqueness {
    SortedAscending,
    SortedDescending,
    AscendingOneRandomSwap,
    Random,
    Random_SomeElementsIdentical,
    AllElementsIdentical
}

class Swapper{
    private Swapper(){}

    public static void swap(int[] arr, int i, int j) {
        int temp = arr[i];
        arr[i] = arr[j];
        arr[j] = temp;
    }
}

//region SortAlgorithms
class MergeSort implements SortAlgorithm {

    public MergeSort(){

    }


    @Override
    public void sort(int[] arr, int maxNumberInArr) {
        mergeSort(arr, 0 , arr.length - 1);
    }

    // Main function that sorts arr[l..r] using
    // merge()
    public static void mergeSort(int[] arr, int l, int r) {
        if (l < r) {
            // Find the middle point
            int m = l + (r - l) / 2;

            // Sort first and second halves
            mergeSort(arr, l, m);
            mergeSort(arr, m + 1, r);

            // Merge the sorted halves
            merge(arr, l, m, r);
        }
    }

    // Merges two subarrays of arr[].
    // First subarray is arr[l..m]
    // Second subarray is arr[m+1..r]
    private static void merge(int[] arr, int l, int m, int r) {
        // Find sizes of two subarrays to be merged
        int n1 = m - l + 1;
        int n2 = r - m;

        /* Create temp arrays */
        int[] L = new int[n1];
        int[] R = new int[n2];

        /* Copy data to temp arrays */
        System.arraycopy(arr, l, L, 0, n1);
        for (int j = 0; j < n2; ++j)
            R[j] = arr[m + 1 + j];

        /* Merge the temp arrays */

        // Initial indexes of first and second subarrays
        int i = 0, j = 0;

        // Initial index of merged subarray array
        int k = l;
        while (i < n1 && j < n2) {
            if (L[i] <= R[j]) {
                arr[k] = L[i];
                i++;
            } else {
                arr[k] = R[j];
                j++;
            }
            k++;
        }

        /* Copy remaining elements of L[] if any */
        while (i < n1) {
            arr[k] = L[i];
            i++;
            k++;
        }

        /* Copy remaining elements of R[] if any */
        while (j < n2) {
            arr[k] = R[j];
            j++;
            k++;
        }
    }
}

class RandomizedQuickSort implements SortAlgorithm {

    private static final Random random = new Random();

    @Override
    public void sort(int[] arr, int maxNumberInArr) {
        randomizedQuickSort(arr, 0, arr.length - 1);
    }

    static int randomizedPartition(int[] A, int p, int r){
        int i = random.nextInt(p, r);
        Swapper.swap(A, i, r);
        int pivot = A[r];
        i = p;
        for (int j = p; j < r; j++)
            if (A[j] <= pivot){
                Swapper.swap(A, i, j);
                i++;
            }
        Swapper.swap(A, i, r);
        return i;
    }

    static void randomizedQuickSort(int[] A, int p, int r){
        if (p < r){
            int q = randomizedPartition(A, p, r);
            randomizedQuickSort(A, p, q-1);
            randomizedQuickSort(A, q+1, r);
        }
    }

}

class CountingSort implements SortAlgorithm {
    @Override
    public void sort(int[] arr, int maxNumberInArr) {
        int range = maxNumberInArr + 1;
        int[] count = new int[range];
        int[] output = new int[arr.length];
        //noinspection ForLoopReplaceableByForEach
        for (int i = 0; i < arr.length; i++) {
            count[arr[i]]++;
        }

        for (int i = 1; i < count.length; i++) {
            count[i] += count[i - 1];
        }

        for (int i = arr.length - 1; i >= 0; i--) {
            output[count[arr[i]] - 1] = arr[i];
            count[arr[i]]--;
        }

        System.arraycopy(output, 0, arr, 0, arr.length);
    }
}

class HeapSort implements SortAlgorithm {
    @Override
    public void sort(int[] arr, int maxNumberInArr) {
        buildHeap(arr);

        for (int swapToPos = arr.length - 1; swapToPos > 0; swapToPos--) {
            // Move root to end
            Swapper.swap(arr, 0, swapToPos);

            // Fix remaining heap
            heapify(arr, swapToPos, 0);
        }
    }

    public void buildHeap(int[] elements) {
        // "Find" the last parent node
        int lastParentNode = elements.length / 2 - 1;

        // Now heapify it from here on backwards
        for (int i = lastParentNode; i >= 0; i--) {
            heapify(elements, elements.length, i);
        }
    }

    // To heapify a subtree rooted with node i which is
    // an index in arr[]. n is size of heap
    public void heapify(int[] arr, int n, int i)
    {
        int largest = i; // Initialize largest as root
        int l = 2 * i + 1; // left = 2*i + 1
        int r = 2 * i + 2; // right = 2*i + 2

        // If left child is larger than root
        if (l < n && arr[l] > arr[largest])
            largest = l;

        // If right child is larger than largest so far
        if (r < n && arr[r] > arr[largest])
            largest = r;

        // If largest is not root
        if (largest != i) {
            Swapper.swap(arr, i, largest);

            // Recursively heapify the affected sub-tree
            heapify(arr, n, largest);
        }
    }
}
//endregion
