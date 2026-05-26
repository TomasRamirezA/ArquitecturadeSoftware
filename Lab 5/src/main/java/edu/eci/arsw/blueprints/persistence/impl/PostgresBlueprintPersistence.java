package edu.eci.arsw.blueprints.persistence.impl;

import edu.eci.arsw.blueprints.model.Blueprint;
import edu.eci.arsw.blueprints.model.Point;
import edu.eci.arsw.blueprints.persistence.BlueprintNotFoundException;
import edu.eci.arsw.blueprints.persistence.BlueprintPersistence;
import edu.eci.arsw.blueprints.persistence.BlueprintPersistenceException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Repository
@Primary
public class PostgresBlueprintPersistence implements BlueprintPersistence {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Override
    public void saveBlueprint(Blueprint bp) throws BlueprintPersistenceException {
        try {
            // Upsert blueprint
            String sqlBlueprint = "INSERT INTO blueprints (author, name) VALUES (?, ?) ON CONFLICT (author, name) DO NOTHING";
            jdbcTemplate.update(sqlBlueprint, bp.getAuthor(), bp.getName());

            // Delete existing points to replace them (naive update approach)
            String sqlDeletePoints = "DELETE FROM points WHERE author = ? AND blueprint_name = ?";
            jdbcTemplate.update(sqlDeletePoints, bp.getAuthor(), bp.getName());

            // Insert new points
            String sqlPoint = "INSERT INTO points (author, blueprint_name, x, y, point_order) VALUES (?, ?, ?, ?, ?)";
            List<Point> points = bp.getPoints();
            for (int i = 0; i < points.size(); i++) {
                Point p = points.get(i);
                jdbcTemplate.update(sqlPoint, bp.getAuthor(), bp.getName(), p.x(), p.y(), i);
            }
        } catch (Exception e) {
            throw new BlueprintPersistenceException("Error saving blueprint: " + e.getMessage());
        }
    }

    @Override
    public Blueprint getBlueprint(String author, String name) throws BlueprintNotFoundException {
        String sqlBlueprint = "SELECT author, name FROM blueprints WHERE author = ? AND name = ?";
        List<Blueprint> blueprints = jdbcTemplate.query(sqlBlueprint, (rs, rowNum) -> {
            return new Blueprint(rs.getString("author"), rs.getString("name"), new ArrayList<>());
        }, author, name);

        if (blueprints.isEmpty()) {
            throw new BlueprintNotFoundException("Blueprint not found: " + author + " - " + name);
        }

        Blueprint bp = blueprints.get(0);

        String sqlPoints = "SELECT x, y FROM points WHERE author = ? AND blueprint_name = ? ORDER BY point_order";
        List<Point> points = jdbcTemplate.query(sqlPoints, (rs, rowNum) -> {
            return new Point(rs.getInt("x"), rs.getInt("y"));
        }, author, name);

        points.forEach(bp::addPoint);

        return bp;
    }

    @Override
    public Set<Blueprint> getBlueprintsByAuthor(String author) throws BlueprintNotFoundException {
        String sqlBlueprints = "SELECT author, name FROM blueprints WHERE author = ?";
        List<Blueprint> blueprintsInDb = jdbcTemplate.query(sqlBlueprints, (rs, rowNum) -> {
            return new Blueprint(rs.getString("author"), rs.getString("name"), new ArrayList<>());
        }, author);

        if (blueprintsInDb.isEmpty()) {
            throw new BlueprintNotFoundException("No blueprints found for author: " + author);
        }

        Set<Blueprint> result = new HashSet<>();
        for (Blueprint bp : blueprintsInDb) {
            result.add(getBlueprint(bp.getAuthor(), bp.getName()));
        }
        return result;
    }

    @Override
    public Set<Blueprint> getAllBlueprints() {
        String sqlBlueprints = "SELECT author, name FROM blueprints";
        List<Blueprint> blueprintsInDb = jdbcTemplate.query(sqlBlueprints, (rs, rowNum) -> {
            return new Blueprint(rs.getString("author"), rs.getString("name"), new ArrayList<>());
        });

        Set<Blueprint> result = new HashSet<>();
        for (Blueprint bp : blueprintsInDb) {
            try {
                result.add(getBlueprint(bp.getAuthor(), bp.getName()));
            } catch (BlueprintNotFoundException e) {
                // Should not happen as we just queried them
            }
        }
        return result;
    }

    @Override
    public void addPoint(String author, String name, int x, int y) throws BlueprintNotFoundException {
        // Verify blueprint exists
        getBlueprint(author, name);

        // Get max order
        String sqlMaxOrder = "SELECT COALESCE(MAX(point_order), -1) FROM points WHERE author = ? AND blueprint_name = ?";
        Integer maxOrder = jdbcTemplate.queryForObject(sqlMaxOrder, Integer.class, author, name);

        String sqlPoint = "INSERT INTO points (author, blueprint_name, x, y, point_order) VALUES (?, ?, ?, ?, ?)";
        jdbcTemplate.update(sqlPoint, author, name, x, y, maxOrder + 1);
    }
}
