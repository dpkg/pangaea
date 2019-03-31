# Change Log

## [3.0.3] (2019-02-20)
- Add @Prematching to EnvironmentModifierFilter to fix "java.lang.IllegalStateException: Method could be called only in pre-matching request filter."

## [3.0.2]
- Update retrofit2 to 2.5.0

## [3.0.1]
- Update to dropwizard 1.3.7

## [3.0.0]
 - Update to dropwizard 1.3.5
 - Remove constructor of SiloTemplateResolver because it causes issues with serialization

## [2.0.0]
 - Update to be compatible with dropwizard 1.3

## [1.0.2]
 - Fixed issue with silo template resolver and serialization of configs

## [1.0.1]
 - Extracted from dropwizard-common so that these components can be shared more easily amongst other internal libraries
