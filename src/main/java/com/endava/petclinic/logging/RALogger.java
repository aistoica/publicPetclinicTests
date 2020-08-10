package com.endava.petclinic.logging;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import io.restassured.filter.Filter;
import io.restassured.filter.FilterContext;
import io.restassured.filter.log.LogDetail;
import io.restassured.internal.print.RequestPrinter;
import io.restassured.internal.print.ResponsePrinter;
import io.restassured.response.Response;
import io.restassured.specification.FilterableRequestSpecification;
import io.restassured.specification.FilterableResponseSpecification;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;

public class RALogger {

    private static ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
	private static final Logger consoleLogger = LogManager.getLogger( "console" );
	private static final Logger fileLogger = LogManager.getLogger( "file" );

	private static final String MESSAGE_SEPARATOR = "~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~\n";

	private RALogger() {
	}

	public static void addToLog( String logMessage ) {
		try {
			byteArrayOutputStream.write( ( logMessage + "\n" ).getBytes() );
		} catch ( IOException e ) {
			e.printStackTrace();
		}
	}

	public static synchronized void logOutput() {
		StringBuilder log = new StringBuilder( byteArrayOutputStream.toString() );

		if ( log.length() != 0 ) {
			for ( String line : log.toString().split( "\n" ) ) {
				fileLogger.info( line.replace( "\r", "" ) );
			}
		}

		byteArrayOutputStream.reset();
	}

	public static class LogFilter implements Filter {

		@Override
		public Response filter( FilterableRequestSpecification requestSpec, FilterableResponseSpecification responseSpec, FilterContext ctx ) {

			Response response = null;

			try {

				// send the request
				response = ctx.next( requestSpec, responseSpec );

			} catch ( Exception e ) {
				addToLog( "Could not connect to the environment" );
				addToLog( e.getMessage() );
				throw new AssertionError( "Could not connect to the environment" );
			} finally {
				// print the request
				RequestPrinter.print( requestSpec, requestSpec.getMethod(), requestSpec.getURI(), LogDetail.ALL, requestSpec.getConfig().getLogConfig().blacklistedHeaders(), new PrintStream( byteArrayOutputStream ), true );
				// add an empty line
				addToLog( "\n" );
				if ( response != null ) {
					// print the response
					ResponsePrinter.print( response, response, new PrintStream( byteArrayOutputStream ), LogDetail.ALL, true, requestSpec.getConfig().getLogConfig().blacklistedHeaders() );
				}
				// add the message separator
				addToLog( MESSAGE_SEPARATOR );

				// print the log
				logOutput();
			}

			return response;
		}
	}
}


