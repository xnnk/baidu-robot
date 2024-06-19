package com.lzx.strangermatching.service.impl;

import com.lzx.strangermatching.entity.SysUser;
import com.lzx.strangermatching.entity.UserInfo;
import com.lzx.strangermatching.repository.elastic.UserInfoRepository;
import com.lzx.strangermatching.request.SearchReq;
import com.lzx.strangermatching.response.R;
import com.lzx.strangermatching.service.UserInfoService;
import com.lzx.strangermatching.util.RUtil;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.common.lucene.search.function.FieldValueFactorFunction;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.functionscore.FunctionScoreQueryBuilder;
import org.elasticsearch.index.query.functionscore.ScoreFunctionBuilder;
import org.elasticsearch.index.query.functionscore.ScoreFunctionBuilders;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.query.NativeSearchQuery;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;

import static com.lzx.strangermatching.util.ShiroUtil.getUserEntity;

/**
 * @ClassName: UserInfoServiceImpl
 * @Description:
 * @Author: LZX
 * @Date: 2023/12/19 16:50
 */
@Service
@Slf4j
@Transactional
public class UserInfoServiceImpl implements UserInfoService {

    @Autowired
    private UserInfoRepository userInfoRepository;

    @Autowired
    private ElasticsearchRestTemplate elasticsearchTemplate;

    @Override
    public R saveUserInfo(UserInfo userInfo) {
        UserInfo byUserId = userInfoRepository.findUserInfoByUserId(userInfo.getUserId());
        if (byUserId == null) {
            userInfoRepository.save(userInfo);
        }
        else {
            userInfoRepository.deleteUserInfoByUserId(userInfo.getUserId());
            BeanUtils.copyProperties(userInfo, byUserId);
            userInfoRepository.save(byUserId);
        }
        return RUtil.success();
    }

    @Override
    public R deleteUserInfo(Integer userId) {
        userInfoRepository.deleteUserInfoByUserId(userId);
        return RUtil.success();
    }

    /**
     * 陌生人匹配
     * @return
     */
    @Override
    public List<UserInfo> search(SearchReq searchReq) {
        SysUser sysUser = getUserEntity();
        Integer userId = sysUser.getId();
        UserInfo userInfo = userInfoRepository.findUserInfoByUserId(userId);

        // 构建ElasticSearch查询
        NativeSearchQuery nativeSearchQuery = buildNativeSearchQuery(userInfo, searchReq);

        List<SearchHit<UserInfo>> searchHits = elasticsearchTemplate.search(nativeSearchQuery, UserInfo.class).toList();

        return searchHits.stream().map(SearchHit::getContent).toList();
    }

    /**
     * 构建 Elasticsearch 查询
     * @param userInfo
     * @param searchReq
     * @return
     */
    private NativeSearchQuery buildNativeSearchQuery(UserInfo userInfo, SearchReq searchReq) {

        // 构建Bool查询
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();

        // 添加性别匹配条件
        Integer sexualOrientation = searchReq.getSexualOrientation();
        if (1 == sexualOrientation || 2 == sexualOrientation) {
            boolQueryBuilder.must(QueryBuilders.matchQuery("sex", sexualOrientation));
        }

        // 添加年龄匹配条件
        Integer ageTendency = searchReq.getAgeTendency();
        if (ageTendency == 1) {
            int maxAge = userInfo.getAge() + 15;
            boolQueryBuilder.must(QueryBuilders.rangeQuery("age").gte(userInfo.getAge()).lte(maxAge));
        } else if (ageTendency == 2) {
            int minAge = Math.max(userInfo.getAge() - 10, 18);
            boolQueryBuilder.must(QueryBuilders.rangeQuery("age").gte(minAge).lte(userInfo.getAge()));
        }

        // 添加身高匹配条件
        Integer heightTendency = searchReq.getHeightTendency();
        int expectedHeight = searchReq.getHeightTendency(); // 从 searchReq 中获取用户期望的身高
        int heightTolerance = 5; // 允许的身高误差范围
        // 调整男女对身高倾向的容忍度
        if (1 == sexualOrientation) {
            boolQueryBuilder.must(
                    QueryBuilders.rangeQuery("height")
                            .gte(expectedHeight - heightTolerance * 3)
                            .lte(expectedHeight + heightTolerance)
            );
        } else if (2 == sexualOrientation) {
            boolQueryBuilder.must(
                    QueryBuilders.rangeQuery("height")
                            .gte(expectedHeight - heightTolerance)
                            .lte(expectedHeight + heightTolerance * 3)
            );
        }

        // 添加收入匹配条件
        Integer incomePropensity = searchReq.getIncomePropensity();
        if (incomePropensity == 1) {
            // 调整修饰因子和修饰符，以匹配比当前用户收入高的用户
            // 需要收入高的用户分数涨幅(k斜率)会更大
            // 构建 Score Function Builder
            ScoreFunctionBuilder<?> incomeScoreFunction = ScoreFunctionBuilders.fieldValueFactorFunction("cash")
                    .modifier(FieldValueFactorFunction.Modifier.LN1P) // 使用 ln(1 + x) 作为修饰符
                    .factor((float) 0.1); // 修饰因子，可以根据实际情况调整

            // 添加 Score Function 到 Function Score Query
            FunctionScoreQueryBuilder.FilterFunctionBuilder incomeFilterFunction = new FunctionScoreQueryBuilder.FilterFunctionBuilder(
                    QueryBuilders.existsQuery("cash"), // 只有存在收入字段的文档才会得分
                    incomeScoreFunction
            );
            boolQueryBuilder.must(QueryBuilders.functionScoreQuery(incomeScoreFunction));
        } else if (incomePropensity == 2) {
            // 调整修饰因子和修饰符，以匹配比当前用户收入低的用户
            // 需要收入低的用户分数涨幅(k斜率)会更小
            // 构建 Score Function Builder
            ScoreFunctionBuilder<?> incomeScoreFunction = ScoreFunctionBuilders.fieldValueFactorFunction("cash")
                    .modifier(FieldValueFactorFunction.Modifier.LN) // 使用 ln(x) 作为修饰符
                    .factor((float) 0.05); // 修饰因子，可以根据实际情况调整

            // 添加 Score Function 到 Function Score Query
            FunctionScoreQueryBuilder.FilterFunctionBuilder incomeFilterFunction = new FunctionScoreQueryBuilder.FilterFunctionBuilder(
                    QueryBuilders.existsQuery("cash"), // 只有存在收入字段的文档才会得分
                    incomeScoreFunction
            );

            boolQueryBuilder.must(QueryBuilders.functionScoreQuery(incomeScoreFunction));
            boolQueryBuilder.must(QueryBuilders.rangeQuery("cash")
                    .gte(Math.max(0, userInfo.getCash() - userInfo.getCash() / 2))
                    .lte(userInfo.getCash() + 3000)
            );
        }

        // 构建NativeSearchQuery
        return new NativeSearchQueryBuilder().withQuery(boolQueryBuilder).build();
    }

    @Override
    public List<UserInfo> scoreByBMI(List<UserInfo> userInfos, SearchReq searchReq) {

        SysUser sysUser = getUserEntity();
        Integer userId = sysUser.getId();
        UserInfo userInfo = userInfoRepository.findUserInfoByUserId(userId);

        // 添加体重匹配条件
        Integer weightTendency = searchReq.getWeightTendency();
        Integer bmiMath = BMIMath(userInfo.getHeight(), userInfo.getWeight());

        int i = 0;

        for (UserInfo user: userInfos) {
            i++;
            Integer bmi = BMIMath(user.getHeight(), user.getWeight());
            if(bmiMath + 1 == bmi || bmiMath - 1 == bmi || bmiMath.equals(bmi)) {
                continue;
            } else {
                userInfos.remove(i - 1);
            }
        }

        return userInfos;
    }

    private Integer BMIMath(Integer height, Integer weight) {
        // 计算 BMI
        double bmi = weight / Math.pow(height, 2);

        // 根据 BMI 分类体型
        // 0:无所谓 1:瘦 2:正常 3:偏胖 4:胖
        int bodyType;
        if (bmi < 18.5) {
            bodyType = 1;
        } else if (bmi < 24) {
            bodyType = 2;
        } else if (bmi < 28) {
            bodyType = 3;
        } else {
            bodyType = 4;
        }

        return bodyType;
    }
}
