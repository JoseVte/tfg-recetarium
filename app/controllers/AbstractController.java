package controllers;

import middleware.Common;
import play.mvc.Controller;
import play.mvc.Security;

@Security.Authenticated(Common.class)
public abstract class AbstractController extends Controller {
}
