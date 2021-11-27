// Autogenerated from library/resources.i

package ideal.library.resources;

import ideal.library.elements.*;
import ideal.library.formats.json_data;

public interface readonly_resource_catalog extends readonly_resource<dictionary<string, resource_identifier>>, any_resource_catalog {
  resource_identifier id();
  resource_identifier resolve(string name);
  resource_identifier resolve(string name, extension ext);
}
