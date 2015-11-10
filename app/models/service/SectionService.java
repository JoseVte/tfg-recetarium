package models.service;

import java.util.List;

import models.Section;
import models.dao.SectionDAO;

public class SectionService {
    /**
     * Create a section
     *
     * @param Section data
     *
     * @return Section
     */
    public static Section create(Section data) {
        return SectionDAO.create(data);
    }

    /**
     * Update a section
     *
     * @param Section data
     *
     * @return Section
     */
    public static Section update(Section data) {
        return SectionDAO.update(data);
    }

    /**
     * Find a section by id
     *
     * @param Integer id
     *
     * @return Section
     */
    public static Section find(Integer id) {
        return SectionDAO.find(id);
    }

    /**
     * Delete a section by id
     *
     * @param Integer id
     */
    public static Boolean delete(Integer id) {
        Section section = SectionDAO.find(id);
        if (section != null) {
            SectionDAO.delete(section);
            return true;
        } else {
            return false;
        }
    }

    /**
     * Get all sections
     *
     * @return List<Section>
     */
    public static List<Section> all() {
        return SectionDAO.all();
    }

    /**
     * Get the page of sections
     *
     * @param Integer page
     * @param Integer size
     *
     * @return List<Section>
     */
    public static List<Section> paginate(Integer page, Integer size) {
        return SectionDAO.paginate(page, size);
    }

    /**
     * Get the number of total of sections
     *
     * @return Long
     */
    public static Long count() {
        return SectionDAO.count();
    }
}
