package com.endava.petclinic;

import com.endava.petclinic.models.Owner;
import com.endava.petclinic.models.Pet;
import com.endava.petclinic.models.PetType;
import com.endava.petclinic.models.Visit;
import com.endava.petclinic.util.EnvReader;
import com.github.javafaker.Faker;
import io.restassured.http.ContentType;
import io.restassured.response.ValidatableResponse;
import org.apache.http.HttpStatus;
import org.junit.jupiter.api.Test;

import java.text.SimpleDateFormat;
import java.util.concurrent.TimeUnit;

import static io.restassured.RestAssured.given;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

public class PetClinicTest {

    private Faker faker = new Faker();

    @Test
    public void firstTest(){

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
                .auth()
                .preemptive()
                .basic( "admin", "admin" )
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
                .auth()
                .preemptive()
                .basic( "admin", "admin" )
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

    @Test
    public void putOwnerTest(){
        Owner owner = new Owner();
        owner.setFirstName( faker.name().firstName() );
        owner.setLastName( faker.name().lastName() );
        owner.setCity( faker.address().city() );
        owner.setAddress( faker.address().streetAddress() );
        owner.setTelephone( faker.number().digits( 10 ) );

        ValidatableResponse ownerResponse = given()
                .baseUri( EnvReader.getBaseUri() )
                .basePath( EnvReader.getBasePath() )
                .port( EnvReader.getPort() )
                .contentType( ContentType.JSON )
                .body( owner )
                .post( "/api/owners" )
                .prettyPeek()
                .then().statusCode( HttpStatus.SC_CREATED );

        owner.setId( ownerResponse.extract().jsonPath().getInt( "id" ) );

        owner.setFirstName( faker.name().firstName() );
        owner.setCity( faker.address().city() );

        given()
                .baseUri( EnvReader.getBaseUri() )
                .basePath( EnvReader.getBasePath() )
                .port( EnvReader.getPort() )
                .contentType( ContentType.JSON )
                .body( owner )
                .pathParam( "ownerId", owner.getId() )
                .put( "/api/owners/{ownerId}" )
                .prettyPeek()
                .then().statusCode( HttpStatus.SC_NO_CONTENT );


        ValidatableResponse getResponse = given()
                .basePath( EnvReader.getBasePath() )
                .baseUri( EnvReader.getBaseUri() )
                .port( EnvReader.getPort() )
                .pathParam( "ownerId", owner.getId() )
                .get( "/api/owners/{ownerId}" )
                .prettyPeek()
                .then().statusCode( HttpStatus.SC_OK );

        Owner actualOwner = getResponse.extract().as( Owner.class );
        assertThat( actualOwner, is( owner ) );
    }

    @Test
    public void postPetTest(){
        //add owner
        Owner owner = new Owner();
        owner.setFirstName( faker.name().firstName() );
        owner.setLastName( faker.name().lastName() );
        owner.setCity( faker.address().city() );
        owner.setAddress( faker.address().streetAddress() );
        owner.setTelephone( faker.number().digits( 10 ) );

        ValidatableResponse postOwnerResponse = given()
                .baseUri( EnvReader.getBaseUri() )
                .basePath( EnvReader.getBasePath() )
                .port( EnvReader.getPort() )
                .contentType( ContentType.JSON )
                .body( owner )
                .post( "/api/owners" )
                .prettyPeek()
                .then().statusCode( HttpStatus.SC_CREATED );

        owner.setId( postOwnerResponse.extract().jsonPath().getInt( "id" ) );

        //add pet type
        PetType petType = new PetType( faker.animal().name() );

        ValidatableResponse postPetTypeResponse = given()
                .baseUri( EnvReader.getBaseUri() )
                .basePath( EnvReader.getBasePath() )
                .port( EnvReader.getPort() )
                .contentType( ContentType.JSON )
                .body( petType )
                .post( "/api/pettypes" )
                .prettyPeek()
                .then().statusCode( HttpStatus.SC_CREATED );

        petType.setId( postPetTypeResponse.extract().jsonPath().getInt( "id" ) );

        //add pet
        SimpleDateFormat formatter = new SimpleDateFormat( "yyyy/MM/dd" );
        String birtDate = formatter.format( faker.date().birthday( 0, 10 ) );
        Pet pet = new Pet( faker.name().firstName(), birtDate, owner, petType );

        ValidatableResponse postPetResponse = given()
                .baseUri( EnvReader.getBaseUri() )
                .basePath( EnvReader.getBasePath() )
                .port( EnvReader.getPort() )
                .contentType( ContentType.JSON )
                .body( pet )
                .post( "/api/pets" )
                .prettyPeek()
                .then().statusCode( HttpStatus.SC_CREATED );

        Pet actualPet = postPetResponse.extract().as( Pet.class );
        assertThat( actualPet, is( pet ) );

        //get owner
        given().baseUri( EnvReader.getBaseUri() )
                .basePath( "petclinicSecured" )
                .port( EnvReader.getPort() )
                .auth().basic( "admin", "admin" )
                .pathParam( "ownerID", owner.getId() )
                .get( "/api/owners/{ownerID}" )
                .prettyPeek()
                .then().statusCode( HttpStatus.SC_OK );


    }

    @Test
    public void postVisitTest(){
        //add owner
        Owner owner = new Owner();
        owner.setFirstName( faker.name().firstName() );
        owner.setLastName( faker.name().lastName() );
        owner.setCity( faker.address().city() );
        owner.setAddress( faker.address().streetAddress() );
        owner.setTelephone( faker.number().digits( 10 ) );

        ValidatableResponse postOwnerResponse = given()
                .baseUri( EnvReader.getBaseUri() )
                .basePath( EnvReader.getBasePath() )
                .port( EnvReader.getPort() )
                .contentType( ContentType.JSON )
                .body( owner )
                .post( "/api/owners" )
                .prettyPeek()
                .then().statusCode( HttpStatus.SC_CREATED );

        owner.setId( postOwnerResponse.extract().jsonPath().getInt( "id" ) );

        //add pet type
        PetType petType = new PetType( faker.animal().name() );

        ValidatableResponse postPetTypeResponse = given()
                .baseUri( EnvReader.getBaseUri() )
                .basePath( EnvReader.getBasePath() )
                .port( EnvReader.getPort() )
                .contentType( ContentType.JSON )
                .body( petType )
                .post( "/api/pettypes" )
                .prettyPeek()
                .then().statusCode( HttpStatus.SC_CREATED );

        petType.setId( postPetTypeResponse.extract().jsonPath().getInt( "id" ) );

        //add pet
        SimpleDateFormat formatter = new SimpleDateFormat( "yyyy/MM/dd" );
        String birtDate = formatter.format( faker.date().birthday( 0, 10 ) );
        Pet pet = new Pet( faker.name().firstName(), birtDate, owner, petType );

        ValidatableResponse postPetResponse = given()
                .baseUri( EnvReader.getBaseUri() )
                .basePath( EnvReader.getBasePath() )
                .port( EnvReader.getPort() )
                .contentType( ContentType.JSON )
                .body( pet )
                .post( "/api/pets" )
                .prettyPeek()
                .then().statusCode( HttpStatus.SC_CREATED );

        pet.setId( postPetResponse.extract().jsonPath().getInt( "id" ) );

        //add visit
        String date = formatter.format( faker.date().past( 10, TimeUnit.DAYS ) );
        Visit visit = new Visit( date, faker.chuckNorris().fact(), pet );

        ValidatableResponse postVisitResponse = given()
                .baseUri( EnvReader.getBaseUri() )
                .basePath( EnvReader.getBasePath() )
                .port( EnvReader.getPort() )
                .contentType( ContentType.JSON )
                .body( visit )
                .post( "api/visits" )
                .prettyPeek()
                .then().statusCode( HttpStatus.SC_CREATED );

        Visit actualVisit = postVisitResponse.extract().as( Visit.class );
        assertThat( actualVisit, is( visit ) );


        //get owner
        given().baseUri( EnvReader.getBaseUri() )
                .basePath( EnvReader.getBasePath() )
                .port( EnvReader.getPort() )
                .pathParam( "ownerID", owner.getId() )
                .get( "/api/owners/{ownerID}" )
                .prettyPeek()
                .then().statusCode( HttpStatus.SC_OK );
    }
}
