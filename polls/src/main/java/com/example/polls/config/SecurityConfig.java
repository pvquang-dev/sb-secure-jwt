package com.example.polls.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.BeanIds;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import com.example.polls.config.security.CustomUserDetailsService;
import com.example.polls.config.security.JwtAuthenticationEntryPoint;
import com.example.polls.config.security.JwtAuthenticationFilter;


/**
 * Created by rajeevkumarsingh on 01/08/17.
 */

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SecurityConfig extends WebSecurityConfigurerAdapter {

  @Autowired
  private CustomUserDetailsService customUserDetailsService;

  @Autowired
  private JwtAuthenticationEntryPoint unauthorizedHandler;

  @Bean
  public JwtAuthenticationFilter jwtAuthenticationFilter() {
    return new JwtAuthenticationFilter();
  }
  
  @Override
  public void configure(AuthenticationManagerBuilder authenticationManagerBuilder)
      throws Exception {
    authenticationManagerBuilder.userDetailsService(customUserDetailsService)
        .passwordEncoder(passwordEncoder());
  }

  @Bean(BeanIds.AUTHENTICATION_MANAGER)
  @Override
  public AuthenticationManager authenticationManagerBean() throws Exception {
    return super.authenticationManagerBean();
  }

  @Bean
  public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
  }

//  @Override
//  protected void configure(HttpSecurity http) throws Exception {
//    http
//    .cors()
//    .and()
//    .csrf()
//    .disable()
//    .authorizeRequests()
//    .antMatchers("/api/auth/**").permitAll()
//    .anyRequest().authenticated()
//    .and()
//    .exceptionHandling()
//    .authenticationEntryPoint(unauthorizedHandler)
//    .and()
//    .sessionManagement()
//    .sessionCreationPolicy(SessionCreationPolicy.STATELESS);
//    // Add our custom JWT security filter
//    http.addFilterBefore(jwtAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class);
//  }

  @Override
  protected void configure(HttpSecurity httpSecurity) throws Exception {
    // We don't need CSRF for this example
    httpSecurity
      .csrf()
        .disable()
        // dont authenticate this particular request
        .authorizeRequests()
        .antMatchers("/api/auth/**").permitAll()
        // all other requests need to be authenticated
        .anyRequest().authenticated()
        .and()
        // make sure we use stateless session; session won't be used to
        // store user's state.
        .exceptionHandling()
        .authenticationEntryPoint(unauthorizedHandler)
        .and()
        .sessionManagement()
        .sessionCreationPolicy(SessionCreationPolicy.STATELESS);
    // Add a filter to validate the tokens with every request
    httpSecurity.addFilterBefore(jwtAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class);
  }
}
