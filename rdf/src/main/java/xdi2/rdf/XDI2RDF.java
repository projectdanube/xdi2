package xdi2.rdf;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import org.openrdf.model.Model;
import org.openrdf.model.URI;
import org.openrdf.model.ValueFactory;
import org.openrdf.model.impl.TreeModel;
import org.openrdf.model.impl.ValueFactoryImpl;

import xdi2.core.Graph;
import xdi2.core.Statement;
import xdi2.core.constants.XDIConstants;
import xdi2.core.exceptions.Xdi2RuntimeException;
import xdi2.core.features.nodetypes.XdiAttribute;
import xdi2.core.features.nodetypes.XdiPeerRoot;
import xdi2.core.syntax.XDIAddress;
import xdi2.core.syntax.XDIArc;
import xdi2.core.syntax.XDIStatement;
import xdi2.core.util.XDIAddressUtil;

public class XDI2RDF {

	public static String PREFIX = "xdi";
	public static String NAMESPACE = "https://xdi.org/";

	public static URI URI_CONTEXT = ValueFactoryImpl.getInstance().createURI(NAMESPACE, "ontology/_context");

	public static Model graphToModel(Iterable<Statement> statements) {

		Model model = new TreeModel();

		model.setNamespace(PREFIX, NAMESPACE);

		try {

			for (Statement statement : statements) {

				XDIStatement XDIstatement = statement.getXDIStatement();

				ValueFactory valueFactory = ValueFactoryImpl.getInstance();

				org.openrdf.model.Resource subject;
				org.openrdf.model.URI predicate;
				org.openrdf.model.Value object;
				org.openrdf.model.Resource context = null;

				XDIAddress rootXDIAddress = XDIAddressUtil.extractXDIAddress(XDIstatement.getContextNodeXDIAddress(), XdiPeerRoot.class, false, false);

				if (rootXDIAddress != null) {

					context = valueFactory.createURI(NAMESPACE, URLEncoder.encode(rootXDIAddress.toString(), "UTF-8"));
				}

				if (XDIstatement.isContextNodeStatement()) {

					XDIAddress subjectXDIAddress = XDIstatement.getContextNodeXDIAddress();
					if (rootXDIAddress != null) subjectXDIAddress = XDIAddressUtil.localXDIAddress(subjectXDIAddress, - rootXDIAddress.getNumXDIArcs());
					if (subjectXDIAddress == null) subjectXDIAddress = XDIConstants.XDI_ADD_ROOT;

					XDIArc objectXDIArc = XDIstatement.getContextNodeXDIArc();

					subject = valueFactory.createURI(NAMESPACE, URLEncoder.encode(subjectXDIAddress.toString(), "UTF-8"));
					predicate = URI_CONTEXT;
					object = valueFactory.createURI(NAMESPACE, URLEncoder.encode(objectXDIArc.toString(), "UTF-8"));
				} else if (XDIstatement.isRelationStatement()) {

					XDIAddress subjectXDIAddress = XDIstatement.getContextNodeXDIAddress();
					if (rootXDIAddress != null) subjectXDIAddress = XDIAddressUtil.localXDIAddress(subjectXDIAddress, - rootXDIAddress.getNumXDIArcs());
					if (subjectXDIAddress == null) subjectXDIAddress = XDIConstants.XDI_ADD_ROOT;

					XDIAddress predicateXDIAddress = XDIstatement.getPredicate();
					XDIAddress objectXDIAddress = XDIstatement.getTargetContextNodeXDIAddress();

					subject = valueFactory.createURI(NAMESPACE, URLEncoder.encode(subjectXDIAddress.toString(), "UTF-8"));
					predicate = valueFactory.createURI(NAMESPACE, URLEncoder.encode(predicateXDIAddress.toString(), "UTF-8"));
					object = valueFactory.createURI(NAMESPACE, URLEncoder.encode(objectXDIAddress.toString(), "UTF-8"));
				} else if (XDIstatement.isLiteralStatement()) {

					XDIAddress subjectXDIAddress = XDIstatement.getContextNodeXDIAddress();
					if (rootXDIAddress != null) subjectXDIAddress = XDIAddressUtil.localXDIAddress(subjectXDIAddress, - rootXDIAddress.getNumXDIArcs());
					if (subjectXDIAddress == null) subjectXDIAddress = XDIConstants.XDI_ADD_ROOT;

					XDIAddress predicateXDIAddress = XDIAddressUtil.extractXDIAddress(subjectXDIAddress, XdiAttribute.class, false, true);

					subjectXDIAddress = XDIAddressUtil.parentXDIAddress(subjectXDIAddress, - predicateXDIAddress.getNumXDIArcs());

					subject = valueFactory.createURI(NAMESPACE, URLEncoder.encode(subjectXDIAddress.toString(), "UTF-8"));
					predicate = valueFactory.createURI(NAMESPACE, URLEncoder.encode(predicateXDIAddress.toString(), "UTF-8"));

					Object literalData = XDIstatement.getLiteralData();

					if (literalData instanceof String) object = valueFactory.createLiteral((String) literalData);
					if (literalData instanceof Number) object = valueFactory.createLiteral(((Number) literalData).doubleValue());
					if (literalData instanceof Boolean) object = valueFactory.createLiteral(((Boolean) literalData).booleanValue());
					else object = valueFactory.createLiteral(literalData.toString());
				} else {

					throw new Xdi2RuntimeException("Invalid statment: " + XDIstatement.toString() + " (" + XDIstatement.getClass().getName() + ")");
				}

				org.openrdf.model.Statement RDFstatement;

				if (context != null) {

					RDFstatement = valueFactory.createStatement(subject, predicate, object, context);
				} else {

					RDFstatement = valueFactory.createStatement(subject, predicate, object);
				}

				model.add(RDFstatement);
			}
		} catch (UnsupportedEncodingException ex) {

			throw new Xdi2RuntimeException(ex.getMessage(), ex);
		}

		return model;
	}

	public static Model graphToModel(Graph graph) {

		return graphToModel(graph.getAllStatements());
	}
}
