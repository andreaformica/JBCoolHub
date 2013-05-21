package atlas.cool.interceptors;

import javax.interceptor.AroundInvoke;
import javax.interceptor.InvocationContext;

public class WebRestCtxProvider {

	@AroundInvoke
    public Object injectMap(InvocationContext ic) throws Exception{
		String orderByCondition = (String) ic.getContextData().get("OrderBy");
        WebRestContextHolder.put("OrderBy",orderByCondition);
        return ic.proceed();
    }
}
