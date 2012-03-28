package filters.utils;

public interface IRange<T extends Number> {
	T getLower();
	T getHigher();
	void setLower(T lower);
	void setHigher(T higher);
}
