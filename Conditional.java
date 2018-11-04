/**
 *  Used to pass a boolean condition as an argument.
 */
public interface Conditional<T>
{
    public boolean condition(T obj);
}