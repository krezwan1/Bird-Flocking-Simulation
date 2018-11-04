import java.util.ArrayList;

/**
 * A utility class that offers helper methods on ArrayLists.
 * Class was taken for the purposes of running a simulation.
 * 
 * Name: Kazi Rezwan
 * Author: Dr. Lee Stemkoski
 * Date: April 26, 2017
 * Course: CSC 302-001
 * Assignment Number: 5
 */
public class Utils
{
    /**
     *  A generic method to generate a sublist of a list
     *  whose elements satisfy a given condition.
     */
    public static <T> ArrayList<T> filter(ArrayList<T> originalList, Conditional<T> test)
    {
        ArrayList<T> filteredList = new ArrayList<T>();
        
        for (T obj : originalList)
        {
            if ( test.condition(obj) )
                filteredList.add(obj);
        }
        
        return filteredList;
    }
    
    public static <T> T getRandomElement(ArrayList<T> originalList)
    {
        int index = (int)(Math.random() * originalList.size());
        return originalList.get(index);
    }
}