package util;

public interface IObserverable {
	public void register(IObserver observer);
	public void unregister(IObserver observer);
	public void notifyObservers();
}
