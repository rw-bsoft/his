package phis.application.war.source.temperature.drawshape.factory;

public class  FactoryLine<T> implements ChartFactory<T> {

	@SuppressWarnings("unchecked")
	@Override
	public T newInstance() {
		return (T) new TemperatureTWSolidLine();
	}

}
