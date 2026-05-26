package edu.eci.arsw.blueprints.controllers;

import edu.eci.arsw.blueprints.model.Blueprint;
import edu.eci.arsw.blueprints.model.Point;
import edu.eci.arsw.blueprints.persistence.BlueprintNotFoundException;
import edu.eci.arsw.blueprints.persistence.BlueprintPersistenceException;
import edu.eci.arsw.blueprints.services.BlueprintsServices;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Set;

@RestController
@RequestMapping("/api/v1/blueprints")
@Tag(name = "Blueprints", description = "Blueprint management APIs")
public class BlueprintsAPIController {

    private final BlueprintsServices services;

    public BlueprintsAPIController(BlueprintsServices services) {
        this.services = services;
    }

    @Operation(summary = "Get all blueprints", description = "Returns a list of all blueprints")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved list")
    })

    @GetMapping
    public ResponseEntity<edu.eci.arsw.blueprints.controllers.ApiResponse<Set<Blueprint>>> getAll() {
        return ResponseEntity.ok(new edu.eci.arsw.blueprints.controllers.ApiResponse<>(200, "Success", services.getAllBlueprints()));
    }

    @Operation(summary = "Get blueprints by author", description = "Returns all blueprints for a specific author")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Found the blueprints"),
            @ApiResponse(responseCode = "404", description = "Author not found")
    })
    @GetMapping("/{author}")
    public ResponseEntity<edu.eci.arsw.blueprints.controllers.ApiResponse<?>> byAuthor(@PathVariable String author) {
        try {
            Set<Blueprint> blueprints = services.getBlueprintsByAuthor(author);
            return ResponseEntity.ok(new edu.eci.arsw.blueprints.controllers.ApiResponse<>(200, "Success", blueprints));
        } catch (BlueprintNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new edu.eci.arsw.blueprints.controllers.ApiResponse<>(404, e.getMessage(), null));
        }
    }
    @Operation(summary = "Get blueprint by author and name", description = "Returns a specific blueprint")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Found the blueprint"),
            @ApiResponse(responseCode = "404", description = "Blueprint not found")
    })

    @GetMapping("/{author}/{bpname}")
    public ResponseEntity<edu.eci.arsw.blueprints.controllers.ApiResponse<?>> byAuthorAndName(
            @PathVariable String author, @PathVariable String bpname) {
        try {
            Blueprint bp = services.getBlueprint(author, bpname);
            return ResponseEntity.ok(new edu.eci.arsw.blueprints.controllers.ApiResponse<>(200, "Success", bp));
        } catch (BlueprintNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new edu.eci.arsw.blueprints.controllers.ApiResponse<>(404, e.getMessage(), null));
        }
    }
    @Operation(summary = "Create a new blueprint", description = "Creates a new blueprint with points")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Blueprint created"),
            @ApiResponse(responseCode = "400", description = "Invalid input or blueprint already exists")
    })

    @PostMapping
    public ResponseEntity<edu.eci.arsw.blueprints.controllers.ApiResponse<?>> add(
            @Valid @RequestBody NewBlueprintRequest req) {
        try {
            Blueprint bp = new Blueprint(req.author(), req.name(), req.points());
            services.addNewBlueprint(bp);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(new edu.eci.arsw.blueprints.controllers.ApiResponse<>(201, "Created successfully", null));
        } catch (BlueprintPersistenceException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new edu.eci.arsw.blueprints.controllers.ApiResponse<>(400,
                            "Error creating blueprint: " + e.getMessage(), null));
        }
    }
    @Operation(summary = "Add a point to a blueprint", description = "Updates an existing blueprint by adding a point")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "202", description = "Point added accepted"),
            @ApiResponse(responseCode = "404", description = "Blueprint not found")
    })
    @PutMapping("/{author}/{bpname}/points")
    public ResponseEntity<edu.eci.arsw.blueprints.controllers.ApiResponse<?>> addPoint(@PathVariable String author,
            @PathVariable String bpname,
            @RequestBody Point p) {
        try {
            services.addPoint(author, bpname, p.x(), p.y());
            return ResponseEntity.status(HttpStatus.ACCEPTED)
                    .body(new edu.eci.arsw.blueprints.controllers.ApiResponse<>(202, "Point added successfully", null));
        } catch (BlueprintNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new edu.eci.arsw.blueprints.controllers.ApiResponse<>(404, e.getMessage(), null));
        }
    }

    public record NewBlueprintRequest(
            @NotBlank String author,
            @NotBlank String name,
            @Valid java.util.List<Point> points) {
    }
}
