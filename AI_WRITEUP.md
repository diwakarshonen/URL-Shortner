# AI Usage Write-up

## How AI Was Used

AI was used as a development assistant during the development of the URL Shortener application.

AI assistance was used for:
- Understanding Spring Boot project structure
- Reviewing implementation approaches
- Debugging errors during development
- Creating automated test cases
- Improving README documentation
- Understanding database flow and API behavior

## What I Decided Myself

The application design and major technical decisions were made by me.

I decided to:
- Build the application using Spring Boot
- Create REST APIs for URL shortening and redirection
- Use H2 database for storing URL mappings
- Use JPA repository for database operations
- Support custom aliases
- Add automated tests for important scenarios

## Changes Made After AI Suggestions

AI suggestions were reviewed and modified based on the project requirements.

Changes made:
- Updated test implementation according to Spring Boot version compatibility
- Fixed test database conflicts by cleaning database data before each test
- Verified API responses manually using HTTP client testing
- Modified documentation based on the implemented features

## Design Decisions and Trade-offs

### Database Selection

H2 database was selected because it is lightweight and suitable for local development and testing.

For a production-level application, databases like MySQL or PostgreSQL can be used.

### Short Code Generation

Short codes are generated to provide compact URLs.

The generated codes are checked to maintain uniqueness and avoid conflicts.

### Testing Strategy

Automated tests were created to verify:

- URL shortening functionality
- Redirect functionality
- Invalid short code handling
- Duplicate URL handling
- Custom alias creation

## Future Improvements

Possible improvements with additional development time:

- Add click analytics
- Add user authentication
- Add URL expiration feature
- Add caching for faster redirects
- Add rate limiting
- Use a production database