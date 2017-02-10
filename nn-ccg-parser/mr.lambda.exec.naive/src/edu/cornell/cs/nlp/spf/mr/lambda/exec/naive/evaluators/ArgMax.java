/*******************************************************************************
 * Copyright (C) 2011 - 2015 Yoav Artzi, All rights reserved.
 * <p>
 * This program is free software; you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation; either version 2 of the License, or any later version.
 * <p>
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 * <p>
 * You should have received a copy of the GNU General Public License along with
 * this program; if not, write to the Free Software Foundation, Inc., 51
 * Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA.
 *******************************************************************************/
package edu.cornell.cs.nlp.spf.mr.lambda.exec.naive.evaluators;

import java.util.HashSet;
import java.util.Set;

import edu.cornell.cs.nlp.spf.mr.lambda.exec.naive.ILambdaResult;
import edu.cornell.cs.nlp.spf.mr.lambda.exec.naive.ILiteralEvaluator;
import edu.cornell.cs.nlp.spf.mr.lambda.exec.naive.Tuple;

public class ArgMax implements ILiteralEvaluator {
	
	@Override
	public Object evaluate(Object[] args) {
		if (args.length == 2 && args[0] instanceof ILambdaResult
				&& args[1] instanceof ILambdaResult) {
			// Case set contains only one entry, return null, since no order is
			// available for a single item
			if (((ILambdaResult) args[0]).size() == 1) {
				return null;
			}
			
			// Collect the entries
			final Set<Object> objects = new HashSet<Object>();
			for (final Tuple tuple : (ILambdaResult) args[0]) {
				if (tuple.numKeys() != 1) {
					return null;
				}
				objects.add(tuple.get(0));
			}
			
			// Iterate over the set generated by the ordering function, get the
			// one with the highest value that is also in objects
			double currentMax = -Double.MAX_VALUE;
			Object currentMaxObject = null;
			for (final Tuple tuple : (ILambdaResult) args[1]) {
				if (!(tuple.getValue() instanceof Double)
						|| tuple.numKeys() != 1) {
					return null;
				}
				
				final double val = (Double) tuple.getValue();
				if (objects.contains(tuple.get(0)) && val > currentMax) {
					currentMax = val;
					currentMaxObject = tuple.get(0);
				}
			}
			
			return currentMaxObject;
		} else {
			return null;
		}
	}
}
