
public class Pair <A, B> 
{
	private A a;
	private B b;
	
	public Pair(A a, B b)
	{
		this.a = a;
		this.b = b;
	}
	
	public A getFirst()
	{
		return a;
	}
	
	public B getSecond()
	{
		return b;
	}
	
	public Pair<A, B> clone()
	{
		return new Pair<A, B>(a, b);
	}
}
