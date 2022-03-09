package at.gv.egiz.pdfas.lib.impl.stamping;

import java.lang.reflect.Member;
import java.lang.reflect.Modifier;
import java.util.Map;

import at.gv.egiz.pdfas.common.settings.IProfileConstants;
import at.gv.egiz.pdfas.common.settings.SignatureProfileSettings;
import at.gv.egiz.pdfas.common.utils.OgnlUtils;
import ognl.AbstractMemberAccess;
import ognl.MemberAccess;
import ognl.OgnlContext;


public class RequestParameterResolver implements IResolver {

	private OgnlContext ctx;
	
	public RequestParameterResolver(Map<String, String> requestParameters) {

		MemberAccess memberAccess = new AbstractMemberAccess() {
			@Override
			public boolean isAccessible(Map context, Object target, Member member, String propertyName) {
				int modifiers = member.getModifiers();
				return Modifier.isPublic(modifiers);
			}
		};

		this.ctx = new OgnlContext(null, null, memberAccess);
		this.ctx.put(IProfileConstants.SIGNATURE_BLOCK_PARAMETER, requestParameters);
	}

	@Override
	public String resolve(String key, String value, SignatureProfileSettings settings) {
		return OgnlUtils.resolvsOgnlExpression(value, this.ctx);
	}

}
