package az.computer.demo.Entity;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeIn;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.annotations.servers.Server;

@OpenAPIDefinition(
        info = @Info(
                title = "Computer Demo API",
                version = "1.0",
                description = "JWT Auth and Local Server Fix"
        ),
        servers = {
                @Server(
                        description = "Local VirtualBox Server",
                        url = "http://localhost:8080"
                ),
                @Server(
                        description = "CloudShell Server (Optional)",
                        url = "https://8080-cs-cd73c805-74e7-4b92-ab0b-47e3eb1b4c29.cs-europe-west4-fycr.cloudshell.dev"
                )
        },
        security = {
                @SecurityRequirement(
                        name = "bearerAuth"
                )
        }
)
@SecurityScheme(
        name = "bearerAuth",
        description = "JWT auth description",
        scheme = "bearer",
        type = SecuritySchemeType.HTTP,
        bearerFormat = "JWT",
        in = SecuritySchemeIn.HEADER
)
public class OpenAPIConfiguration {
}