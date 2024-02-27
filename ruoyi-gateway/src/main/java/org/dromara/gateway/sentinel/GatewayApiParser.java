package org.dromara.gateway.sentinel;

import com.alibaba.csp.sentinel.adapter.gateway.common.api.ApiDefinition;
import com.alibaba.csp.sentinel.adapter.gateway.common.api.ApiPathPredicateItem;
import com.alibaba.csp.sentinel.adapter.gateway.common.api.ApiPredicateItem;
import com.alibaba.csp.sentinel.datasource.Converter;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.nacos.common.utils.CollectionUtils;

import java.util.HashSet;
import java.util.Set;

/**
 * 参考链接: https://www.cnblogs.com/sdliangi/p/16393528.html
 *
 * @author: zhou shuai
 * @date: 2022/10/28 15:12
 */
public class GatewayApiParser implements Converter<String, Set<ApiDefinition>> {

    @Override
    public Set<ApiDefinition> convert(String source) {
        Set<ApiDefinition> apiDefinitionSet = new HashSet<>();
        Set<JSONObject> set = JSON.parseObject(source, Set.class);
        if (CollectionUtils.isNotEmpty(set)) {
            for (JSONObject o : set) {
                ApiDefinition apiDefinition = new ApiDefinition();
                apiDefinition.setApiName(o.getString("apiName"));
                JSONArray predicateItems = o.getJSONArray("predicateItems");
                Set<ApiPredicateItem> apiPredicateItems = new HashSet<>();
                if (CollectionUtils.isNotEmpty(predicateItems)) {
                    for (Object item : predicateItems) {
                        JSONObject object = (JSONObject) item;
                        ApiPathPredicateItem apiPathPredicateItem = new ApiPathPredicateItem();
                        apiPathPredicateItem.setMatchStrategy(object.getIntValue("matchStrategy"));
                        apiPathPredicateItem.setPattern(object.getString("pattern"));
                        apiPredicateItems.add(apiPathPredicateItem);
                    }
                }
                apiDefinition.setPredicateItems(apiPredicateItems);
                apiDefinitionSet.add(apiDefinition);
            }
        }
        return apiDefinitionSet;
    }

}
