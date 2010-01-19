package hr.fer.zemris.ga_framework.model;

import hr.fer.zemris.ga_framework.model.impl.ArithmeticValueHandler;
import hr.fer.zemris.ga_framework.model.impl.LastValueHandler;







public class ValueHandlerFactory {
	
	public static IValueHandler createHandler(HandlerTypes htype, ParameterTypes ptype) {
		switch (htype) {
		case Average:
		case Maximal:
		case Median:
		case Minimal:
		case Sum:
		case StandardDeviation:
			return new ArithmeticValueHandler(ptype, htype);
		case Last:
			return new LastValueHandler();
		}
		throw new IllegalArgumentException("Unknown handler type.");
	}

}














