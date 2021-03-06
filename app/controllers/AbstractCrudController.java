package controllers;

import play.mvc.Result;

public abstract class AbstractCrudController extends AbstractController {
    /**
     * Get the model with pagination
     *
     * @param page   Integer
     * @param size   Integer
     * @param search Search
     * @param order  Search
     *
     * @return Result
     */
    public abstract Result list(Integer page, Integer size, String search, String order);

    /**
     * Get one model by id
     *
     * @param id Integer
     *
     * @return Result
     */
    public abstract Result get(Integer id);

    /**
     * Create a model with the data of request
     *
     * @return Result
     */
    public abstract Result create();

    /**
     * Update a model with the data of request
     *
     * @param id Integer
     *
     * @return Result
     */
    public abstract Result update(Integer id);

    /**
     * Delete a model by id
     *
     * @param id Integer
     *
     * @return Result
     */
    public abstract Result delete(Integer id);
}
