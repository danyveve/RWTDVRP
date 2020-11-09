package com.marcu.vrp.frontend.configuration;


import com.marcu.vrp.backend.model.UserRole;
import com.marcu.vrp.backend.model.UserRoleName;
import com.marcu.vrp.frontend.security.LogoutSuccess;
import com.marcu.vrp.frontend.security.MySavedRequestAwareAuthenticationSuccessHandler;
import com.marcu.vrp.frontend.security.MyUserDetailsService;
import com.marcu.vrp.frontend.security.RestAuthenticationEntryPoint;
import com.marcu.vrp.frontend.security.SimpleAuthenticationFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@ComponentScan("com.marcu.vrp.frontend.security")
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    private final RestAuthenticationEntryPoint restAuthenticationEntryPoint;

    private final MySavedRequestAwareAuthenticationSuccessHandler
            mySavedRequestAwareAuthenticationSuccessHandler;

    private final LogoutSuccess logoutSuccess;

    private final MyUserDetailsService myUserDetailsService;

    public SecurityConfig(RestAuthenticationEntryPoint restAuthenticationEntryPoint, MySavedRequestAwareAuthenticationSuccessHandler mySavedRequestAwareAuthenticationSuccessHandler, LogoutSuccess logoutSuccess, MyUserDetailsService myUserDetailsService) {
        this.restAuthenticationEntryPoint = restAuthenticationEntryPoint;
        this.mySavedRequestAwareAuthenticationSuccessHandler = mySavedRequestAwareAuthenticationSuccessHandler;
        this.logoutSuccess = logoutSuccess;
        this.myUserDetailsService = myUserDetailsService;
    }


    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public DaoAuthenticationProvider authProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService());
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) {
        auth.authenticationProvider(authProvider());
    }


    @Override
    protected UserDetailsService userDetailsService() {
        return myUserDetailsService;
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .addFilterBefore(authenticationFilter(), UsernamePasswordAuthenticationFilter.class)
                .csrf().disable()
                .exceptionHandling()
                .authenticationEntryPoint(restAuthenticationEntryPoint)
                .and()
                .authorizeRequests()
                .antMatchers("/login", "/", "/test/public", "/api/user/register").permitAll()
                .antMatchers("/api/driver/add", "/api/driver/edit", "/api/driver/delete", "/api/vrp/delete").hasAnyRole(UserRoleName.ADMIN.toString(), UserRoleName.DEVELOPER.toString())
                .antMatchers("/test/private", "/api/driver", "/api/vrp", "/api/user").authenticated()
                .anyRequest().authenticated()
                .and()
                .formLogin()
                .successHandler(mySavedRequestAwareAuthenticationSuccessHandler)
                .failureHandler(new SimpleUrlAuthenticationFailureHandler())
                .and()
                .logout()
                .logoutSuccessHandler(logoutSuccess)
                .deleteCookies("JSESSIONID")
                .invalidateHttpSession(false)
                .permitAll();
        http.cors();

    }

    @Bean
    public MySavedRequestAwareAuthenticationSuccessHandler mySuccessHandler() {
        return mySavedRequestAwareAuthenticationSuccessHandler;
    }

    @Bean
    public SimpleUrlAuthenticationFailureHandler myFailureHandler() {
        return new SimpleUrlAuthenticationFailureHandler();
    }

    public SimpleAuthenticationFilter authenticationFilter() throws Exception {
        SimpleAuthenticationFilter filter = new SimpleAuthenticationFilter();
        filter.setAuthenticationManager(authenticationManagerBean());
        filter.setAuthenticationFailureHandler(new SimpleUrlAuthenticationFailureHandler());
        filter.setAuthenticationSuccessHandler(mySavedRequestAwareAuthenticationSuccessHandler);
        return filter;
    }
}

