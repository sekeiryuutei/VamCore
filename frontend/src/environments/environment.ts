/**
 * Variables de entorno de DESARROLLO.
 * `platformName` y `productNames` deben mantenerse sincronizados con
 * /product.config.yml en la raíz del repositorio.
 */
export const environment = {
  production: false,
  apiBaseUrl: 'http://localhost:8080/api/v1',
  platformName: 'VamCore',
  companyName: 'CodeVam',
  productNames: {
    inventory: 'VamStock',
    assets: 'VamAsset',
    logistics: 'VamTrack',
  },
};
