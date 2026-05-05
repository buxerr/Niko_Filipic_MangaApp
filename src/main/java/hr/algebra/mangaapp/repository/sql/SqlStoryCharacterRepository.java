package hr.algebra.mangaapp.repository.sql;

import hr.algebra.mangaapp.exception.RepositoryException;
import hr.algebra.mangaapp.model.StoryCharacter;
import hr.algebra.mangaapp.repository.StoryCharacterRepository;
import hr.algebra.mangaapp.util.DatabaseUtils;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class SqlStoryCharacterRepository extends SqlAbstractRepository<StoryCharacter> implements StoryCharacterRepository {

    @Override
    protected String getFindAllSql() {
        return "SELECT * FROM fn_find_all_story_characters()";
    }

    @Override
    protected String getFindByIdSql() {
        return "SELECT * FROM fn_find_story_character_by_id(?)";
    }

    @Override
    protected String getCreateSql() {
        return "SELECT fn_create_story_character(?, ?)";
    }

    @Override
    protected String getUpdateSql() {
        return "CALL sp_update_story_character(?, ?, ?)";
    }

    @Override
    protected String getDeleteSql() {
        return "CALL sp_delete_story_character(?)";
    }

    @Override
    protected StoryCharacter map(ResultSet resultSet) throws SQLException {
        return new StoryCharacter(
                resultSet.getLong("id"),
                resultSet.getString("first_name"),
                resultSet.getString("last_name")
        );
    }

    @Override
    protected void setCreateParameters(PreparedStatement statement, StoryCharacter character) throws SQLException {
        statement.setString(1, character.getFirstName());
        statement.setString(2, character.getLastName());
    }

    @Override
    protected void setUpdateParameters(CallableStatement statement, StoryCharacter character) throws SQLException {
        statement.setLong(1, character.getId());
        statement.setString(2, character.getFirstName());
        statement.setString(3, character.getLastName());
    }

    @Override
    public List<StoryCharacter> search(String query) {
        List<StoryCharacter> characters = new ArrayList<>();
        String sql = "SELECT * FROM fn_search_story_characters(?)";

        try (
                Connection connection = DatabaseUtils.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql)
        ) {
            statement.setString(1, query);

            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    characters.add(map(resultSet));
                }
            }
        } catch (SQLException e) {
            throw new RepositoryException("Error while searching story characters", e);
        }

        return characters;
    }
}