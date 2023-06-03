package cart.ui;

import cart.db.dao.MemberDao;
import cart.db.entity.MemberEntity;
import cart.domain.member.Member;
import cart.exception.AuthenticationException;
import org.apache.tomcat.util.codec.binary.Base64;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import static cart.exception.ErrorCode.NOT_AUTHENTICATION_MEMBER;

public class MemberArgumentResolver implements HandlerMethodArgumentResolver {

    private static final String AUTH_TYPE = "basic";

    private final MemberDao memberDao;

    public MemberArgumentResolver(MemberDao memberDao) {
        this.memberDao = memberDao;
    }

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.getParameterType().equals(Member.class);
    }

    @Override
    public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer, NativeWebRequest webRequest, WebDataBinderFactory binderFactory) throws Exception {
        String authorization = webRequest.getHeader(HttpHeaders.AUTHORIZATION);
        if (authorization == null) {
            return null;
        }

        String[] authHeader = authorization.split(" ");
        if (!authHeader[0].equalsIgnoreCase(AUTH_TYPE)) {
            return null;
        }

        byte[] decodedBytes = Base64.decodeBase64(authHeader[1]);
        String decodedString = new String(decodedBytes);

        String[] credentials = decodedString.split(":");
        String email = credentials[0];
        String password = credentials[1];

        MemberEntity memberEntity = memberDao.findByName(email)
                .orElseThrow(() -> new AuthenticationException(NOT_AUTHENTICATION_MEMBER));
        Member member = new Member(memberEntity.getId(), memberEntity.getName(), memberEntity.getPassword());
        if (!member.checkPassword(password)) {
            throw new AuthenticationException(NOT_AUTHENTICATION_MEMBER);
        }
        return member;
    }
}
