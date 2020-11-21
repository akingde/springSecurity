package com.tang.config;

import com.tang.oauth.UserNameUserDetailsServiceImpl;
import com.tang.oauth.provider.CustomUserAuthenticationProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.DelegatingPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.sql.DataSource;

/**
 * WEB安全方面的配置
 */
@Configuration
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    private DataSource dataSource;

    @Autowired
    private UserNameUserDetailsServiceImpl userNameUserDetailsService;

    @Override
    @Bean
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    /**
     * 配置用户账户的认证方式。进行密码加密，访问方式是数据库
     * 显然，我们把用户存在了数据库中希望配置JDBC的方式。
     * 此外，我们还配置了使用BCryptPasswordEncoder哈希来保存用户的密码（生产环境中，用户密码肯定不能是明文保存的）
     *
     * @param auth 身份认证管理器
     * @throws Exception 异常信息
     */
    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.jdbcAuthentication()
                .dataSource(dataSource)
                // 密码加密
                .passwordEncoder(new BCryptPasswordEncoder());
    }

    /**
     * 路径安全配置
     * 开放/login和/oauth/authorize两个路径的匿名访问。前者用于登录，后者用于换授权码，这两个端点访问的时机都在登录之前。
     * 设置/login使用表单验证进行登录。
     *
     * @param http htt安全
     * @throws Exception 异常信息
     */
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.authorizeRequests()
                // 匿名访问的路径（忽略的路径）
                .antMatchers("/login", "/oauth/authorize")
                .permitAll()
                // 其他任何请求都要进行认证
                .anyRequest().authenticated()
                .and()
                // 登陆页面
                .formLogin().loginPage("/login");
        // 内部维护的是一个 List<AuthenticationProvider>，多次调用，多次添加
        // 为什么这个认证只对用户名密码模式有效，因为默认的DaoAuthenticationProvider验证的就是用户名密码模式
        http.authenticationProvider(cusutomProvider());
        // 实现其他自定义的方式进行认证？？？是通过什么来判断的？
//        http.authenticationProvider(kerberosServiceAuthenticationProvider());
    }


//    @Bean
//    public KerberosServiceAuthenticationProvider kerberosServiceAuthenticationProvider() {
//        KerberosServiceAuthenticationProvider provider = new KerberosServiceAuthenticationProvider();
//        provider.setTicketValidator(sunJaasKerberosTicketValidator());
//        provider.setUserDetailsService(kerUserDetailService);
//        return provider;
//    }


    /**
     * 自定义登陆验证方式
     */
    @Bean
    public AuthenticationProvider cusutomProvider() {
        CustomUserAuthenticationProvider customUserAuthenticationProvider = new CustomUserAuthenticationProvider();
        customUserAuthenticationProvider.setUserDetailsService(userNameUserDetailsService);
        customUserAuthenticationProvider.setPasswordEncoder(passwordEncoder());
        return customUserAuthenticationProvider;
    }


    /**
     * 密码加密算法
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        DelegatingPasswordEncoder delegatingPasswordEncoder = (DelegatingPasswordEncoder) PasswordEncoderFactories.createDelegatingPasswordEncoder();
        // 设置defaultPasswordEncoderForMatches为BCryptPasswordEncoder，如果需要兼容,则设计为兼容就好
        delegatingPasswordEncoder.setDefaultPasswordEncoderForMatches(new BCryptPasswordEncoder());
        return delegatingPasswordEncoder;
    }
}