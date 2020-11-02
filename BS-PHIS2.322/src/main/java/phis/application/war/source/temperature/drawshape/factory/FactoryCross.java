package phis.application.war.source.temperature.drawshape.factory;


public class FactoryCross<T> implements ChartFactory<T> {

	@SuppressWarnings("unchecked")
	@Override
	public T newInstance() {
		return (T) new TemperatureCross();
	}

}
