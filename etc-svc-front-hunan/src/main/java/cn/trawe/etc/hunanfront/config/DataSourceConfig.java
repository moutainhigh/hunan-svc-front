package cn.trawe.etc.hunanfront.config;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;

import com.alibaba.druid.pool.DruidDataSource;

import cn.trawe.pay.common.config.HashShardingAlgorithmSixteen;
import cn.trawe.pay.common.config.HashShardingAlgorithmSixtyFour;
import cn.trawe.pay.common.config.HashShardingAlgorithmThirtyTwo;
import cn.trawe.pay.common.config.OwnerCodeShardingAlgorithm;
import io.shardingsphere.api.config.rule.ShardingRuleConfiguration;
import io.shardingsphere.api.config.rule.TableRuleConfiguration;
import io.shardingsphere.api.config.strategy.StandardShardingStrategyConfiguration;
import io.shardingsphere.shardingjdbc.api.ShardingDataSourceFactory;

/**
 * 
 * @author daifei
 *
 */
@Configuration
public class DataSourceConfig {


    @Bean
    @ConfigurationProperties(prefix = "master")
    public DataSource getMasterDateSource() {
        return new DruidDataSource();
    }

    @Bean
    @Qualifier("masterDataSource")
    
    @Primary
    public DataSource masterDataSource() throws SQLException {

        Map<String, DataSource> dataSourceMap = new HashMap<>(2);

        dataSourceMap.put("masterDataSource", getMasterDateSource());

        
        // 配置issue_etc_card表规则
        TableRuleConfiguration issueEtcCardTableRuleConfig = new TableRuleConfiguration();
        issueEtcCardTableRuleConfig.setLogicTable("issue_etc_card");
        // 配置分库 + 分表策略
        issueEtcCardTableRuleConfig.setTableShardingStrategyConfig(new StandardShardingStrategyConfiguration("owner_code", new OwnerCodeShardingAlgorithm()));

        // 配置issue_etc_card表规则
        TableRuleConfiguration seconeIssueProcessTableRuleConfig = new TableRuleConfiguration();
        seconeIssueProcessTableRuleConfig.setLogicTable("second_issue_process");
        // 配置分库 + 分表策略
        seconeIssueProcessTableRuleConfig.setTableShardingStrategyConfig(new StandardShardingStrategyConfiguration("owner_code", new OwnerCodeShardingAlgorithm()));

        // 配置etc_user_alipay_agreement表规则
        TableRuleConfiguration agreementTableRuleConfig = new TableRuleConfiguration();
        agreementTableRuleConfig.setLogicTable("etc_user_alipay_agreement");
        // 配置分库 + 分表策略
        agreementTableRuleConfig.setTableShardingStrategyConfig(new StandardShardingStrategyConfiguration("alipay_user_id", new HashShardingAlgorithmThirtyTwo()));
        
        
        // 配置issue_order_log表规则
        TableRuleConfiguration issueOrderLogTableRuleConfig = new TableRuleConfiguration();
        issueOrderLogTableRuleConfig.setLogicTable("issue_order_log");
        // 配置分库 + 分表策略
        issueOrderLogTableRuleConfig.setTableShardingStrategyConfig(new StandardShardingStrategyConfiguration("order_no", new HashShardingAlgorithmSixtyFour()));
        
        // 配置issue_order_resubmit_log表规则
        TableRuleConfiguration issueOrderResubmitLogTableRuleConfig = new TableRuleConfiguration();
        issueOrderResubmitLogTableRuleConfig.setLogicTable("issue_order_resubmit_log");
        // 配置分库 + 分表策略
        issueOrderResubmitLogTableRuleConfig.setTableShardingStrategyConfig(new StandardShardingStrategyConfiguration("order_no", new HashShardingAlgorithmSixteen()));

        
     // 配置etc_user_info表规则
        TableRuleConfiguration etcUserInfoTableRuleConfig = new TableRuleConfiguration();
        etcUserInfoTableRuleConfig.setLogicTable("etc_user_info");
        // 配置分库 + 分表策略
        etcUserInfoTableRuleConfig.setTableShardingStrategyConfig(new StandardShardingStrategyConfiguration("alipay_user_id", new HashShardingAlgorithmThirtyTwo()));
        // 配置分片规则
        ShardingRuleConfiguration shardingRuleConfig = new ShardingRuleConfiguration();
        shardingRuleConfig.getTableRuleConfigs().add(issueEtcCardTableRuleConfig);
        shardingRuleConfig.getTableRuleConfigs().add(seconeIssueProcessTableRuleConfig);
        shardingRuleConfig.getTableRuleConfigs().add(agreementTableRuleConfig);
        shardingRuleConfig.getTableRuleConfigs().add(issueOrderLogTableRuleConfig);
        shardingRuleConfig.getTableRuleConfigs().add(issueOrderResubmitLogTableRuleConfig);
        shardingRuleConfig.getTableRuleConfigs().add(etcUserInfoTableRuleConfig);


        DataSource dataSource = ShardingDataSourceFactory.createDataSource(dataSourceMap, shardingRuleConfig, new ConcurrentHashMap(16), new Properties());
        return dataSource;
    }

    @Bean(name="masterJdbcTemplate")
    public JdbcTemplate masterJdbcTemplate (
            @Qualifier("masterDataSource")  DataSource dataSource ) {

        return new JdbcTemplate(dataSource);
    }

}
