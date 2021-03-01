import java.util.*;

public class Main {
    public static <T extends Comparable> T[] bubbleSort(T[] array)
    {
        T [] result = array.clone();
        int count = result.length;
        for(int i = 0 ;i<count;i++)
        {
            T temp;
            for(int j = 0 ;j<result.length - 1 - i;j++)
            {
                if (result[j].compareTo(result[j+1])>0)
                {
                    temp = result[j];
                    result[j] = result[j+1];
                    result[j+1] = temp;
                }
            }
        }
        return result;
    }
    public static <T extends Comparable> T[] insertionSort(T[] array)
    {
        T [] result = array.clone();
        T temp;
        for (int i = 1; i < result.length; i++)
        {
            temp = result[i];
            int j = i;
            for (; j > 0 && (temp.compareTo(result[j - 1]) < 0); j--)
            {
                result[j] = result[j-1];
            }
            result[j] = temp;
        }
        return  result;
    }
    public static <T extends Comparable> T[] selectionSort(T[] array)
    {
        T [] result = array.clone();
        T max;
        for (int i = 0; i < result.length; i++)
        {
            max = result[0];
            int no = 0;
            for(int j = 0; j < result.length - i; j++)
            {
                if (max.compareTo(result[j]) < 0)
                {
                    max = result[j];
                    no = j;
                }
            }
            result[no] = result[result.length-i-1];
            result[result.length-i-1] = max;
        }
        return result;
    }

    public static <T extends Comparable> boolean check(T[] array)
    {
        for(int i = 0; i < array.length -1; i++)
        {
            if (array[i].compareTo(array[i+1]) > 0 )
            {
                return false;
            }
        }
        return true;
    }


    public static void main(String[] args) {
        Random r1 = new Random();
        Integer [] array = new Integer[10000];
        for (int i = 0; i< array.length; i++)
        {
            array[i] = r1.nextInt(10000000);
        }
        System.out.println("Raw   "+Arrays.toString(array));


        long startTime=System.currentTimeMillis();
        Integer [] bubbleResult = bubbleSort(array);
        long bubbleTime = System.currentTimeMillis() - startTime;
        startTime=System.currentTimeMillis();
        Integer [] insertionResult = insertionSort(array);
        long insertionTime = System.currentTimeMillis() - startTime;
        startTime=System.currentTimeMillis();
        Integer [] selectionResult = selectionSort(array);
        long selectionTime = System.currentTimeMillis() - startTime;



        System.out.println("bubble:     "+check(bubbleResult)+"  time "+bubbleTime+" ms");
        System.out.println("insertion:  "+check(insertionResult)+"  time "+insertionTime+" ms");
        System.out.println("selection:  "+check(selectionResult)+"  time "+selectionTime+" ms");
    }
}