package com.endava.petclinic;

import static io.restassured.RestAssured.given;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

import org.apache.http.HttpStatus;
import org.junit.jupiter.api.Test;

import com.endava.petclinic.logging.RALogger;
import com.endava.petclinic.models.Owner;
import com.endava.petclinic.models.User;
import com.endava.petclinic.util.EnvReader;
import com.github.javafaker.Faker;

import io.restassured.http.ContentType;
import io.restassured.response.ValidatableResponse;

public class PetClinicTest {

	private Faker faker = new Faker();

	@Test
	public void firstTest() {

		// create new User
		User user = new User( faker.internet().password(), faker.name().username(), "OWNER_ADMIN" );
		given().filters( new RALogger.LogFilter() )
				.baseUri( EnvReader.getBaseUri() )
				.port( EnvReader.getPort() )
				.basePath( EnvReader.getBasePath() )
				.auth().preemptive().basic( "admin", "admin" )
				.contentType( ContentType.JSON )
				.body( user )
				.post( "/api/users" )
				.then().statusCode( HttpStatus.SC_CREATED );

		// create new Owner
		Owner owner = new Owner();
		owner.setAddress( faker.address().streetAddress() );
		owner.setCity( faker.address().city() );
		owner.setFirstName( faker.name().firstName() );
		owner.setLastName( faker.name().lastName() );
		owner.setTelephone( faker.number().digits( 10 ) );

		ValidatableResponse response = given().filters( new RALogger.LogFilter() )
				.baseUri( EnvReader.getBaseUri() )
				.port( EnvReader.getPort() )
				.basePath( EnvReader.getBasePath() )
				.auth().preemptive().basic( user.getUsername(), user.getPassword() )
				.contentType( ContentType.JSON )
				.body( owner )
				.post( "/api/owners" )
				.then().statusCode( HttpStatus.SC_CREATED );

		Integer id = response.extract().jsonPath().getInt( "id" );

		// get owner by id
		ValidatableResponse getResponse = given().filters( new RALogger.LogFilter() )
				//request
				.baseUri( EnvReader.getBaseUri() )
				.port( EnvReader.getPort() )
				.basePath( EnvReader.getBasePath() )
				.auth().preemptive().basic( user.getUsername(), user.getPassword() )
				.pathParam( "ownerId", id )
				.get( "/api/owners/{ownerId}" )
				//response
				.then().statusCode( HttpStatus.SC_OK );

		Owner actualOwner = getResponse.extract().as( Owner.class );

		assertThat( actualOwner, is( owner ) );
	}
}
