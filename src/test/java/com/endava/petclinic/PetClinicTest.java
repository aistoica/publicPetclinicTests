package com.endava.petclinic;

import static io.restassured.RestAssured.given;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.equalToIgnoringCase;
import static org.hamcrest.Matchers.hasLength;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.startsWith;

import org.apache.http.HttpStatus;
import org.junit.jupiter.api.Test;

import com.endava.petclinic.models.Owner;
import com.endava.petclinic.util.EnvReader;
import com.github.javafaker.Faker;

import io.restassured.http.ContentType;
import io.restassured.response.ValidatableResponse;

public class PetClinicTest {

	private Faker faker = new Faker();

	@Test
	public void firstTest() {

		Owner owner = new Owner();
		owner.setAddress( faker.address().streetAddress() );
		owner.setCity( faker.address().city() );
		owner.setFirstName( faker.name().firstName() );
		owner.setLastName( faker.name().lastName() );
		owner.setTelephone( faker.number().digits( 10 ) );

		ValidatableResponse response = given()
				.baseUri( EnvReader.getBaseUri() )
				.port( EnvReader.getPort() )
				.basePath( EnvReader.getBasePath() )
				.contentType( ContentType.JSON )
				.body( owner )
				.log().all()
				.post( "/api/owners" )
				.prettyPeek()
				.then().statusCode( HttpStatus.SC_CREATED );

		Integer id = response.extract().jsonPath().getInt( "id" );

		ValidatableResponse getResponse = given()
				//request
				.baseUri( EnvReader.getBaseUri() )
				.port( EnvReader.getPort() )
				.basePath( EnvReader.getBasePath() )
				.pathParam( "ownerId", id )
				.log().all()
				.get( "/api/owners/{ownerId}" )
				//response
				.prettyPeek()
				.then().statusCode( HttpStatus.SC_OK )
				.body( "id", is( id ) )
				.body( "firstName", is( owner.getFirstName() ) )
				.body( "lastName", is( owner.getLastName() ) )
				.body( "address", is( owner.getAddress() ) )
				.body( "city", is( owner.getCity() ) )
				.body( "telephone", is( owner.getTelephone() ) );

		Owner actualOwner = getResponse.extract().as( Owner.class );

		assertThat( actualOwner, is( owner ) );

	}
}
