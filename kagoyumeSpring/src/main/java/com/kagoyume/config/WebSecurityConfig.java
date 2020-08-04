package com.kagoyume.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import com.kagoyume.service.UserDetailsServiceImpl;


@Configuration
@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {


    @Autowired
    private UserDetailsServiceImpl userDetailsService;

    //フォームの値と比較するDBから取得したパスワードは暗号化されているのでフォームの値も暗号化するために使用
    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();
        return bCryptPasswordEncoder;
    }

    @Override
    public void configure(WebSecurity web) throws Exception {
        web.ignoring().antMatchers(	//トップページで読み込むリソースファイルへのアクセスを許可
                            "/images/**",
                            "/css/**",
                            "/js/**"
                            );
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
            .authorizeRequests()	// 認可の設定
            	.antMatchers("/kagoyume/**").permitAll()
                .anyRequest().authenticated() //全てのページは認証が必要、という設定
                .and()
                .csrf().disable()	// CSRF token無効化
            .formLogin()	// ログイン設定
                .loginPage("/kagoyume/login") // ログインフォームのパス
                .loginProcessingUrl("/sign_in") // 認証処理のパス。このURLへリクエストが送られると認証処理が実行される
                .usernameParameter("name") //nameのパラメータ名
                .passwordParameter("password")	//passwordのパラメータ名
                .defaultSuccessUrl("/kagoyume/top", false)	// 認証成功時の遷移先
                .failureUrl("/kagoyume/login?error")	// 認証失敗時の遷移先
                .permitAll()	//誰でもアクセスOK
                .and()
            .logout()	// ログアウト設定
                .logoutUrl("/logout")	//ログアウトパス
                .logoutSuccessUrl("/kagoyume/top")	//ログアウト成功時の遷移先
                .invalidateHttpSession(true)	// ログアウト時のセッション破棄を有効化
                .permitAll();
    }

    @Autowired
    public void configure(AuthenticationManagerBuilder auth) throws Exception{
    	// 認証するユーザーを設定する
        auth.userDetailsService(userDetailsService)
        // 入力値をbcryptでハッシュ化した値でパスワード認証を行う
        .passwordEncoder(passwordEncoder());
    }

}