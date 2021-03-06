import java.io.*;
import java.util.*;
import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.ANTLRFileStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.tree.ParseTreeWalker;

public class Query {
    public static void query(CompoundSymbol datatype, String filename) {
	try {
	    ANTLRInputStream input = new ANTLRFileStream(filename);
	    SchemaLexer lexer = new SchemaLexer(input);
	    CommonTokenStream tokens = new CommonTokenStream(lexer);
	    SchemaParser parser = new SchemaParser(tokens);

	    ParserRuleContext tree = parser.queryfile();
	    QueryVisitor visitor = new QueryVisitor(datatype);
	    visitor.visit(tree);
	} catch (java.io.IOException e) {
	    System.err.println(e);
	}
    }
}

class Constraint {
    public enum Comparison { NotEquals, Equals, MoreThan, LessThan, LessOrEqual, MoreOrEqual };
    public Attribute _attr;
    public Comparison _comp;
    public Entry _value;

    public boolean typeCompatCheck( Attribute attr, Entry value ) {
	Symbol.Type attrType = attr.getActualType();
	Entry.Type valueType = value.getType();

	return ((attrType == Symbol.Type.IntType && valueType == Entry.Type.Number) ||
	        (attrType == Symbol.Type.BoolType && valueType == Entry.Type.Bool) ||
		(attrType == Symbol.Type.StrType && valueType == Entry.Type.String));
    }

    public Constraint( Attribute attr, Comparison comp, Entry value ) {
	if (typeCompatCheck(attr, value)) {
	    _attr = attr;
	    _comp = comp;
	    _value = value;
	}
	else {
	    // Type exception
	}
    }

    public String toString() {
	String ret = "";
	ret += _attr.toString();
	switch (_comp) {
	    case NotEquals: ret += "!="; break;
	    case Equals: ret += "="; break;
	    case MoreThan: ret += ">"; break;
	    case LessThan: ret += "<"; break;
	    case LessOrEqual: ret += "<<"; break;
	    case MoreOrEqual: ret += ">>"; break;
	}
	ret += _value.toString();
	return ret;
    }
}


class QNode {
    public Entry _value;
    public Constraint _constraint;
    public ArrayList<Constraint> _constraints;
    public Attribute _attr;
    public Constraint.Comparison _comp;

    public QNode(Entry value) { _value = value; }
    public QNode(Constraint constraint) { _constraint = constraint; }
    public QNode(ArrayList<Constraint> constraints) { _constraints = constraints; }
    public QNode(Attribute attr) { _attr = attr; }
    public QNode(Constraint.Comparison comp) { _comp = comp; }
}


class QueryVisitor extends SchemaBaseVisitor<QNode> {
    public CompoundSymbol _sym;

    public QueryVisitor( CompoundSymbol sym ) {
	_sym = sym;
    }

    @Override
    public QNode visitGetQuery(SchemaParser.GetQueryContext ctx) {
	ArrayList<Constraint> cons = visit(ctx.constraints())._constraints;
	Shelf matches = initialize(cons);

	for (Constraint current : cons)
	    matches = filter(matches, current);

	printOut(cons, matches);
	return null;
    }


    public Shelf initialize(ArrayList<Constraint> cons) {
	Shelf matches;
	for (Constraint current : cons) {
	    if (current._attr.getActualType() == Symbol.Type.StrType) {
		matches = findMatch(current);
		return matches;
	    }
	}
	for (Constraint current : cons) {
	    if (current._attr.getActualType() == Symbol.Type.IntType) {
		matches = findMatch(current);
		return matches;
	    }
	}
	for (Constraint current : cons) {
	    if (current._attr.getActualType() == Symbol.Type.BoolType) {
		matches = findMatch(current);
		return matches;
	    }
	}
	return null;
    }

    public void printOut(ArrayList<Constraint> cons, Shelf matches) {
	System.out.printf("Query Criteria: %s\n", cons.toString());
	System.out.printf("Successfully found %d %ss meeting criterion-set.\n\n", matches.size(), _sym._name);
	int i = 0;
	for (ArrayList<Entry> a : matches) {
	    System.out.printf("Match #%d:\n", (++i));
	    int s = a.size();
	    for (int j = 0; j < s; ++j) {
		System.out.printf("\t%s: %s\n", _sym._attrs.get(j)._name, a.get(j).toString());
	    }
	    System.out.println();
	}
    }

    public Shelf findMatch(Constraint con) {
	Attribute attr = con._attr;
	Entry val = con._value;
	Constraint.Comparison comp = con._comp;
	if (con._attr.getActualType() == Symbol.Type.BoolType) { return getAll(); }
	else {
	    Shelf matches = new Shelf();
	    AVLTree tree = _sym._trees.get(_sym._atIndex.get(attr._name).intValue());
	    //tree.print();
	    switch (comp) {
		case Equals: return tree.getEqual(matches, val, tree.getRoot());
		case NotEquals: return tree.getNotEqual(matches, val, tree.getRoot());
		case MoreThan: return tree.getMoreThan(matches, val, tree.getRoot());
		case LessThan: return tree.getLessThan(matches, val, tree.getRoot());
		case MoreOrEqual: return tree.getMoreOrEqual(matches, val, tree.getRoot());
		case LessOrEqual: return tree.getLessOrEqual(matches, val, tree.getRoot());
		default: return matches;
	    }
	}
    }

    public Shelf getAll() {
	Shelf matches = new Shelf();
	int numAttrs = _sym._attrs.size();

	for (int i = 0; i < numAttrs; ++i) {
	    Attribute attr = _sym._attrs.get(i);
	    if (attr.getActualType() != Symbol.Type.BoolType) {
		AVLTree tree = _sym._trees.get(i);
		return tree.getAll(matches, tree.getRoot());
	    }
	}
	return null;
    }

    public Shelf filter(Shelf pool, Constraint con) {
	Shelf newpool = new Shelf();

	for (ArrayList<Entry> obj : pool) {
	   if (testConstraint(obj, con)) {
	       newpool.add(obj);
	   }
	}

	return newpool;
    }

    public boolean testConstraint(ArrayList<Entry> obj, Constraint con) {
	int index = _sym._atIndex.get(con._attr._name).intValue();
	Entry objval = obj.get(index);
	Entry val = con._value;

	switch (con._comp) {
	    case Equals: return (objval.equals(val));
	    case NotEquals: return !(objval.equals(val));
	    case LessThan: return (objval.compareTo(val) < 0);
	    case MoreThan: return (objval.compareTo(val) > 0);
	    case LessOrEqual: return (objval.compareTo(val) <= 0);
	    case MoreOrEqual: return (objval.compareTo(val) >= 0);
	    default: return true;
	}
    }
    @Override
	public QNode visitEvalConstraint(SchemaParser.EvalConstraintContext ctx) { 
	    String attrname = (ctx.ID()).getText();
	    Attribute attr = _sym._attrs.get(_sym._atIndex.get(attrname));
	    Constraint.Comparison comp = visit(ctx.comp())._comp;
	    Entry val = visit(ctx.value())._value;
	    return new QNode(new Constraint(attr, comp, val));
	}

    @Override
	public QNode visitEqualTo(SchemaParser.EqualToContext ctx) {
	    return new QNode(Constraint.Comparison.Equals);
	}

    @Override
	public QNode visitNotEqualTo(SchemaParser.NotEqualToContext ctx) {
	    return new QNode(Constraint.Comparison.NotEquals);
	}

    @Override
	public QNode visitLessThan(SchemaParser.LessThanContext ctx) {
	    return new QNode(Constraint.Comparison.LessThan);
	}

    @Override
	public QNode visitMoreThan(SchemaParser.MoreThanContext ctx) {
	    return new QNode(Constraint.Comparison.MoreThan);
	}

    @Override
	public QNode visitAtLeast(SchemaParser.AtLeastContext ctx) {
	    return new QNode(Constraint.Comparison.MoreOrEqual);
	}

    @Override
	public QNode visitAtMost(SchemaParser.AtMostContext ctx) {
	    return new QNode(Constraint.Comparison.LessOrEqual);
	}

    @Override
	public QNode visitGetFirstConstraint(SchemaParser.GetFirstConstraintContext ctx) {
	    Constraint c = visit(ctx.constraint())._constraint;
	    ArrayList<Constraint> constraints = new ArrayList<Constraint>();
	    constraints.add(c);
	    return new QNode(constraints);
	}

    @Override
	public QNode visitGetNextConstraint(SchemaParser.GetNextConstraintContext ctx) {
	    Constraint c = visit(ctx.constraint())._constraint;
	    ArrayList<Constraint> constraints = visit(ctx.constraints())._constraints;
	    constraints.add(c);
	    return new QNode(constraints);
	}

    @Override
	public QNode visitEvalNumberValue(SchemaParser.EvalNumberValueContext ctx) {
	    int number = new Integer((ctx.NUMBER()).getText()).intValue();
	    NumberEntry val = new NumberEntry(number);
	    return new QNode(val);
	}

    @Override
	public QNode visitEvalQStringValue(SchemaParser.EvalQStringValueContext ctx) {
	    String qstr = (ctx.QSTRING()).getText();
	    StringEntry val = new StringEntry(QString.unQuote(qstr));
	    return new QNode(val);
	}

    @Override 
	public QNode visitEvalBoolValue(SchemaParser.EvalBoolValueContext ctx) { 
	    boolean bool = new Boolean((ctx.v).getText()).booleanValue();
	    BoolEntry val = new BoolEntry(bool);
	    return new QNode(val);
	}
}
