package controllers;

import play.mvc.Controller;
import play.mvc.Result;

public abstract class AbstractController extends Controller {
    /**
     * Get the model with pagination
     *
     * @param Integer page
     * @param Integer size
     *
     * @return Result
     */
    public abstract Result list(Integer page, Integer size);
    
    /**
     * Get one model by id
     *
     * @param Integer id
     *
     * @return Result
     */
    public abstract Result get(Integer id);
    
    /**
     * Create an user with the data of request
     *
     * @return Result
     */
    public abstract Result create();
    
    /**
     * Update an user with the data of request
     *
     * @param Integer id
     *
     * @return Result
     */
    public abstract Result update(Integer id);
    
    /**
     * Delete an user by id
     *
     * @param Integer id
     *
     * @return Result
     */
    public abstract Result delete(Integer id);
}
