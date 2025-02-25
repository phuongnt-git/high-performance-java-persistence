package com.vladmihalcea.book.hpjp.util.providers;

import com.vladmihalcea.book.hpjp.util.ReflectionUtils;
import com.vladmihalcea.book.hpjp.util.providers.queries.Queries;
import org.hibernate.dialect.Dialect;

import java.util.Properties;
import javax.sql.DataSource;

/**
 * @author Vlad Mihalcea
 */
public interface DataSourceProvider {

	enum IdentifierStrategy {
		IDENTITY,
		SEQUENCE
	}

	String hibernateDialect();

	DataSource dataSource();

	Class<? extends DataSource> dataSourceClassName();

	Properties dataSourceProperties();

	String url();

	String username();

	String password();

	Database database();

	Queries queries();

	default Class<? extends Dialect> hibernateDialectClass() {
		return ReflectionUtils.getClass(hibernateDialect());
	}
}
