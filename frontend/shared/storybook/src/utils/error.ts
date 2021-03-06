import { APIError } from '@insight/types';

export const mockApiError = (apiError: APIError): Error => {
  const error = new Error('APIError');
  Object.assign(error, {
    response: {
      json: () => ({ error: apiError }),
    },
  });

  return error;
};
