import javax.inject.Inject;

import play.api.mvc.EssentialFilter;
import play.http.HttpFilters;
import play.filters.cors.CORSFilter;

class Filters implements HttpFilters {

    @Inject
    CORSFilter corsFilter;

    public EssentialFilter[] filters() {
        return new EssentialFilter[] { corsFilter };
    }
}
