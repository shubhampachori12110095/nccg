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
package edu.cornell.cs.nlp.spf.atis.parser.ccg.rules.typeshifting;

import edu.cornell.cs.nlp.spf.ccg.categories.Category;
import edu.cornell.cs.nlp.spf.ccg.categories.ICategoryServices;
import edu.cornell.cs.nlp.spf.ccg.categories.syntax.Syntax;
import edu.cornell.cs.nlp.spf.mr.lambda.Literal;
import edu.cornell.cs.nlp.spf.mr.lambda.LogicLanguageServices;
import edu.cornell.cs.nlp.spf.mr.lambda.LogicalConstant;
import edu.cornell.cs.nlp.spf.mr.lambda.LogicalExpression;
import edu.cornell.cs.nlp.spf.mr.language.type.Type;
import edu.cornell.cs.nlp.spf.parser.ccg.rules.IUnaryParseRule;
import edu.cornell.cs.nlp.spf.parser.ccg.rules.ParseRuleResult;
import edu.cornell.cs.nlp.spf.parser.ccg.rules.SentenceSpan;
import edu.cornell.cs.nlp.spf.parser.ccg.rules.UnaryRuleName;

/**
 * AP : f:<ev,t> => AP : (a:<<ev,t>,ev> f)
 *
 * @author Yoav Artzi
 * @author Luke Zettlemoyer
 */
public class PluralAPTypeShifting implements IUnaryParseRule<LogicalExpression> {
	private static final long		serialVersionUID	= -5757442039515430720L;
	private final LogicalConstant	aPred;
	private final Type				EVT;
	private final UnaryRuleName		name;

	public PluralAPTypeShifting(
			ICategoryServices<LogicalExpression> categoryServices) {
		this(categoryServices, "shift_ap_plural");
	}

	public PluralAPTypeShifting(
			ICategoryServices<LogicalExpression> categoryServices, String name) {
		this.name = UnaryRuleName.create(name);
		this.EVT = LogicLanguageServices.getTypeRepository()
				.getTypeCreateIfNeeded("<ev,t>");
		this.aPred = (LogicalConstant) categoryServices
				.readSemantics("a:<<ev,t>,ev>");
	}

	@Override
	public ParseRuleResult<LogicalExpression> apply(
			Category<LogicalExpression> category, SentenceSpan span) {
		if (category.getSyntax().equals(Syntax.AP)) {
			final LogicalExpression raisedSemantics = typeShiftSemantics(category
					.getSemantics());
			if (raisedSemantics != null) {
				return new ParseRuleResult<LogicalExpression>(name,
						Category.create(Syntax.AP, raisedSemantics));
			}
		}
		return null;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		final PluralAPTypeShifting other = (PluralAPTypeShifting) obj;
		if (EVT == null) {
			if (other.EVT != null) {
				return false;
			}
		} else if (!EVT.equals(other.EVT)) {
			return false;
		}
		if (aPred == null) {
			if (other.aPred != null) {
				return false;
			}
		} else if (!aPred.equals(other.aPred)) {
			return false;
		}
		if (name == null) {
			if (other.name != null) {
				return false;
			}
		} else if (!name.equals(other.name)) {
			return false;
		}
		return true;
	}

	@Override
	public UnaryRuleName getName() {
		return name;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (EVT == null ? 0 : EVT.hashCode());
		result = prime * result + (aPred == null ? 0 : aPred.hashCode());
		result = prime * result + (name == null ? 0 : name.hashCode());
		return result;
	}

	@Override
	public boolean isValidArgument(Category<LogicalExpression> category,
			SentenceSpan span) {
		return Syntax.AP.equals(category.getSyntax());
	}

	public LogicalExpression typeShiftSemantics(LogicalExpression sem) {
		if (sem.getType().isExtendingOrExtendedBy(EVT)) {
			final LogicalExpression[] args = new LogicalExpression[1];
			args[0] = sem;
			return new Literal(aPred, args);
		}
		return null;
	}
}
