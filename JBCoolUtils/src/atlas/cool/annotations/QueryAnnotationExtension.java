/**
 * 
 */
package atlas.cool.annotations;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.Set;
import java.util.logging.Logger;

import javax.enterprise.event.Observes;
import javax.enterprise.inject.spi.AnnotatedConstructor;
import javax.enterprise.inject.spi.AnnotatedField;
import javax.enterprise.inject.spi.AnnotatedMethod;
import javax.enterprise.inject.spi.AnnotatedType;
import javax.enterprise.inject.spi.Extension;
import javax.enterprise.inject.spi.ProcessAnnotatedType;
import javax.inject.Inject;
import javax.persistence.Entity;

/**
 * @author formica
 * 
 */
public class QueryAnnotationExtension implements Extension {

	/**
	 * 
	 */
	@Inject
	private Logger log;

	/**
	 * @param pat
	 */
	<X> void processAnnotatedType(@Observes ProcessAnnotatedType<X> pat) {

		// wrap this to override the annotations of the class

		final AnnotatedType<X> at = pat.getAnnotatedType();

		AnnotatedType<X> wrapped = new AnnotatedType<X>() {

			@Override
			public Set<AnnotatedConstructor<X>> getConstructors() {

				return at.getConstructors();

			}

			@Override
			public Set<AnnotatedField<? super X>> getFields() {

				return at.getFields();

			}

			@Override
			public Class<X> getJavaClass() {

				return at.getJavaClass();

			}

			@Override
			public Set<AnnotatedMethod<? super X>> getMethods() {

				return at.getMethods();

			}

			/* (non-Javadoc)
			 * @see javax.enterprise.inject.spi.Annotated#getAnnotation(java.lang.Class)
			 */
			public <T extends Annotation> T getAnnotation(
					final Class<T> annType) {

				if (Entity.class.equals(annType)) {
					log.info("Processing annotation " + at.toString()
							+ " type " + annType.getSimpleName());
				}
				return at.getAnnotation(annType);
			}

			@Override
			public Set<Annotation> getAnnotations() {

				return at.getAnnotations();
			}

			@Override
			public Type getBaseType() {

				return at.getBaseType();
			}

			@Override
			public Set<Type> getTypeClosure() {

				return at.getTypeClosure();
			}

			@Override
			public boolean isAnnotationPresent(
					final Class<? extends Annotation> annType) {

				return at.isAnnotationPresent(annType);
			}
		};

		pat.setAnnotatedType(wrapped);
	}

}