package com.endava.petclinic.models;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class User {

	private Boolean enabled;
	private String password;
	private String username;
	private List<Role> roles;

	public User() {
	}

	public User( String password, String username, String... roles ) {
		this.enabled = true;
		this.password = password;
		this.username = username;
		this.roles = new ArrayList<>();

		for( String roleName : roles ) {
			Role role = new Role( roleName );
			this.roles.add( role );
		}
	}

	public Boolean getEnabled() {
		return enabled;
	}

	public void setEnabled( Boolean enabled ) {
		this.enabled = enabled;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword( String password ) {
		this.password = password;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername( String username ) {
		this.username = username;
	}

	public List<Role> getRoles() {
		return roles;
	}

	public void setRoles( List<Role> roles ) {
		this.roles = roles;
	}

	@Override
	public boolean equals( Object o ) {
		if ( this == o )
			return true;
		if ( o == null || getClass() != o.getClass() )
			return false;
		User user = (User) o;
		return Objects.equals( enabled, user.enabled ) &&
				Objects.equals( password, user.password ) &&
				Objects.equals( username, user.username ) &&
				Objects.equals( roles, user.roles );
	}

	@Override
	public int hashCode() {
		return Objects.hash( enabled, password, username, roles );
	}

	@Override
	public String toString() {
		return "User{" +
				"enabled=" + enabled +
				", password='" + password + '\'' +
				", username='" + username + '\'' +
				", roles=" + roles +
				'}';
	}
}
